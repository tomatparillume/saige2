/*
 * Copyright(c) 2024, Billtrust Inc., All rights reserved worldwide
 */
package com.parillume.external.chat.model;

import com.parillume.external.model.QueryBuilder;
import com.parillume.model.CompanyModel;
import com.parillume.model.CorpusModel;
import com.parillume.model.external.User;
import com.parillume.model.internal.AssessmentResult;
import com.parillume.model.internal.AssessmentType;
import com.parillume.model.internal.EnnResult;
import com.parillume.model.internal.MBResult;
import com.parillume.util.StringUtil;
import com.parillume.util.model.CorpusUtil;
import com.parillume.util.webapp.WebAppDisplayUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class StructuredChatGPTQueryBuilder extends QueryBuilder {
    private ChatTopic topic;
    
    public StructuredChatGPTQueryBuilder(ChatMetadata chatMetadata, CompanyModel company, CorpusModel corpus) {
        super(chatMetadata, company, corpus);
        setTopic(chatMetadata.getChatTopic());
    }
    
    @Override
    public void generateQuery() throws Exception {       
        Optional<User> chatterOpt = getCompany().getUsers().stream()
                                                .filter(u -> StringUtil.nullEquals(getChatMetadata().getChatterUserId(), u.getId()))
                                                .findFirst();
        if(chatterOpt.isEmpty())
            throw new Exception("Chatter not found"); 
        User chatter = chatterOpt.get();
        
        List<String> targetUserIds = getChatMetadata().getTargetUserIds();
        
        List<User> targetUsers = getCompany().getUsers().stream()
                                           .filter(u -> targetUserIds.contains(u.getId()))
                                           .collect(Collectors.toList());
        
        if(targetUsers.size() != targetUserIds.size()) {
            targetUserIds.removeAll( targetUsers.stream().map(u->u.getId()).collect(Collectors.toList()) );
            throw new Exception("Referenced user(s) not found: " + targetUserIds);
        }
                                         
        WebAppDisplayUtil.sortUsersByName(targetUsers); 
        
        Map<String,AssessmentResult> ennResultsCorpus = CorpusUtil.getAssessmentResultsById(getCorpus(), AssessmentType.Enneagram);
        Map<String,AssessmentResult> mbResultsCorpus = CorpusUtil.getAssessmentResultsById(getCorpus(), AssessmentType.MyersBriggs);
        Map<String,AssessmentResult> csResultsCorpus = CorpusUtil.getAssessmentResultsById(getCorpus(), AssessmentType.CliftonStrengths);
        
        List<AssessmentType> chatAssessments = getChatMetadata().getAssessmentTypes();
        boolean chatAboutEnn = chatAssessments.contains(AssessmentType.Enneagram);
        boolean chatAboutMB = chatAssessments.contains(AssessmentType.MyersBriggs);
        boolean chatAboutCS = chatAssessments.contains(AssessmentType.CliftonStrengths);
        
        List<String> userPrompts = new ArrayList<>();
        String chatterPrompt = getUserPrompt(chatAboutEnn, ennResultsCorpus,
                                             chatAboutMB, mbResultsCorpus, 
                                             chatAboutCS, csResultsCorpus,
                                             chatter, true);
        if(chatterPrompt == null)
            throw new Exception("Chatter " + chatter.getId() + " has no assessment results");
        else
            userPrompts.add(chatterPrompt);  
            
        for(User user: targetUsers) {
            String userPrompt = getUserPrompt(chatAboutEnn, ennResultsCorpus,
                                              chatAboutMB, mbResultsCorpus, 
                                              chatAboutCS, csResultsCorpus,
                                              user, false);
            if(userPrompt != null)
                userPrompts.add(userPrompt);  
        }
        
        String promptPreamble = "In the following question: The term 'superpowers' references the person's personal gifts, exceptional abilities, and talents; " +
                                "the term 'kryptonite' references thinking styles, tasks, and challenges that are naturally difficult for the person; " +
                                "the term 'play-full practice' references a new way of thinking, acting, and responding that the person regularly practices.";
        
        String promptAppendix = "Please provide me with advice in short form, with no more than 3 bullet points.";
        
        getChatMetadata().setChatQuery( promptPreamble + " " + 
                                        String.join(" ", userPrompts) + " " + 
                                        topic.getQuestion(getChatMetadata().getTargetUserIds().size()==1) + " " +
                                        promptAppendix);
    }    

    private String getUserPrompt(boolean chatAboutEnn, Map<String,AssessmentResult> ennResultsCorpus,
                                 boolean chatAboutMB, Map<String,AssessmentResult> mbResultsCorpus, 
                                 boolean chatAboutCS, Map<String,AssessmentResult> csResultsCorpus,
                                 User user, boolean userIsChatter) {
            
        String prefix = userIsChatter ?
                        "I have " :
                        user.getNameFirst() + " " + user.getNameLast() + " has ";
        
        StringBuilder userPrompt = new StringBuilder();
        userPrompt.append(prefix);
        userPrompt.append("the following personality-assessment scores: ");

        List<String> userEnnResultIds = CorpusUtil.getResultIdsForUser(ennResultsCorpus, user);
        List<String> userMBResultIds = CorpusUtil.getResultIdsForUser(mbResultsCorpus, user);
        List<String> userCSResultIds = CorpusUtil.getResultIdsForUser(csResultsCorpus, user);

        boolean userHasEnn = !userEnnResultIds.isEmpty();
        boolean userHasMB = !userMBResultIds.isEmpty();
        boolean userHasCS = !userCSResultIds.isEmpty(); 
        if(!userHasEnn && !userHasMB && !userHasCS)
            return null;

        if( !(userHasEnn && chatAboutEnn) &&
            !(userHasMB && chatAboutMB) &&
            !(userHasCS && chatAboutCS) 
        ) {            
            // The chatAssessments (e.g. "Chat about Enneagram!") don't
            // match any user assessment data; chat about an assessment that
            // the user DOES have.
                 if(userHasEnn) chatAboutEnn = true;
            else if(userHasMB)  chatAboutMB = true;
            else if(userHasCS)  chatAboutCS = true;
        }

        if(userHasEnn && chatAboutEnn) {
            EnnResult ennResult = (EnnResult) ennResultsCorpus.get(userEnnResultIds.get(0));
            userPrompt.append("Enneagram ");
            userPrompt.append(ennResult.getName());
            userPrompt.append("; ");
        }

        if(userHasMB && chatAboutMB) {
            MBResult mbResult = (MBResult) mbResultsCorpus.get(userMBResultIds.get(0));
            userPrompt.append("MyersBriggs ");
            userPrompt.append(mbResult.getName());
            userPrompt.append("; ");
        } 

        if(userHasCS && chatAboutCS) {
            userPrompt.append("CliftonStrengths ");
            String csNames = String.join(", ", 
                                         userCSResultIds.stream()
                                                        .map(s -> csResultsCorpus.get(s).getName())
                                                        .collect(Collectors.toList()) );
            userPrompt.append(csNames);
            userPrompt.append("; ");
        }

        List<String> playfullPractices = user.getPlayfullPractices();
        if(playfullPractices != null && !playfullPractices.isEmpty()) {
            userPrompt.append(" ");
            userPrompt.append(prefix);
            userPrompt.append("the following play-full practices: ");
            userPrompt.append( user.getPlayfullPractices().stream().collect(Collectors.joining("; ")) );
            userPrompt.append(".");
        }
        
        return userPrompt.toString();
    }
}
