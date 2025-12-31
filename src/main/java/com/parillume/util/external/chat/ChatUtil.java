/*
 * Copyright(c) 2024, Billtrust Inc., All rights reserved worldwide
 */
package com.parillume.util.external.chat;

import com.parillume.external.chat.model.ChatMetadata;
import com.parillume.external.chat.model.ChatTopic;
import com.parillume.model.internal.AssessmentType;
import com.parillume.util.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class ChatUtil {
    /**
     * Given the submitted chatHistory, generate a new ChatMetadata.
     */
    public static ChatMetadata generateNextChatMetadata(List<ChatMetadata> chatHistory, 
                                                        String chatterUserId,
                                                        List<String> targetUserIds) {
        
        List<AssessmentType> allAssessmentTypes = new ArrayList( Arrays.asList(AssessmentType.values()) );
        AssessmentType randomAssessmentType = allAssessmentTypes.get( Constants.RANDOM.nextInt(allAssessmentTypes.size()) );
        
        ////// If the chatterUserId has never had a chat, create a chat and return:
        if(chatHistory == null || chatHistory.isEmpty()) {
            return new ChatMetadata(chatterUserId, targetUserIds.get(0), ChatTopic.getDefaultTopic(), randomAssessmentType);
        }
        
        
        // Sort by ascending date: earliest first
        chatHistory = chatHistory.stream()
                                 .sorted(Comparator.comparing(ChatMetadata::getQueryDateMs))
                                 .collect(Collectors.toList());
        
        LinkedList<ChatTopic> allTopics = ChatTopic.getOrderedTopics();
        
        Map<ChatTopic, Set<String>> topicToUserIds = allTopics.stream()
                                                              .collect( // Set all values to new HashSet:
                                                                Collectors.toMap(t -> t, t -> new HashSet<>())
                                                              );        
        
        Map<String,Set<AssessmentType>> userIdToAssessmentTypes = new HashMap<>();
        
        for(ChatMetadata chatMetadata: chatHistory) {
            topicToUserIds.get( chatMetadata.getChatTopic() )
                          .addAll( chatMetadata.getTargetUserIds() );

            for(String userId: chatMetadata.getTargetUserIds()) {
                Set<AssessmentType> types = userIdToAssessmentTypes.get(userId);
                if(types == null) {
                    types = new HashSet();
                    userIdToAssessmentTypes.put(userId, types);
                }
                types.addAll(chatMetadata.getAssessmentTypes());
            }
        }         
        
        LinkedList<ChatTopic> coveredTopics = new LinkedList( topicToUserIds.entrySet()
                                                            .stream()
                                                            .filter(e -> !e.getValue().isEmpty())
                                                            .map(e -> e.getKey())
                                                            .collect(Collectors.toList())
                                                  );
        
        Set<String> coveredUserIds = topicToUserIds.values()
                                                   .stream()
                                                   .flatMap(l -> l.stream())
                                                   .collect(Collectors.toSet());
        
        boolean allTopicsCovered = coveredTopics.containsAll(allTopics);
        boolean allUserIdsCovered = targetUserIds.containsAll(coveredUserIds);
        
        // Select a topic
        ChatTopic nextTopic = null;  
        if(!allTopicsCovered) {
            // Choose an uncovered topic
            allTopics.removeAll(coveredTopics);  
            nextTopic = allTopics.getFirst();
            
        } else {
            // We've covered all the topics; choose the next one in line
            nextTopic = ChatTopic.getNextTopic(allTopics.getLast());
        }
        
        if(!allUserIdsCovered && coveredUserIds.size() < targetUserIds.size()) {
            // Trim targetUserIds to contain only uncovered ids
            targetUserIds.removeAll(coveredUserIds);
        } // else: 
          // All user ids have been covered; we'll select an already-covered id
          
        String nextTargetUserId = targetUserIds.get( Constants.RANDOM.nextInt(targetUserIds.size()) );  
        
        /****** CORNER CASE ****************************************************
         *                      SOME TOPICS UNCOVERED   ALL TOPICS COVERED   
         *                      ---------------------   ------------------
         * SOME IDS UNCOVERED           handled             handled
         * 
         * ALL IDS COVERED              handled            CORNER CASE
         */
        // All topics and user ids have been covered; select an uncovered 
        // topic|id combination. If none exist, pick a random user id.
        if(allTopicsCovered && allUserIdsCovered) {
            for(ChatTopic topic: ChatTopic.getOrderedTopicsStartingWith(nextTopic)) {
                Set<String> topicUserIds = topicToUserIds.get(topic);
                
                // If any target user ids have not covered this topic yet:
                if(!topicUserIds.containsAll(targetUserIds)) {
                    // Redefine nextTopic
                    nextTopic = topic;
                    
                    // Trim targetUserIds to contain only uncovered ids for this topic
                    targetUserIds.removeAll(topicUserIds);
                    break;
                }
            }
            
            if(!targetUserIds.isEmpty())
                nextTargetUserId = targetUserIds.get( Constants.RANDOM.nextInt(targetUserIds.size()) );  
        }    
        /***********************************************************************/
        
        ////// Find an assessment not recently covered for the nextTargetUserId
        Set<AssessmentType> coveredTypesForUser = userIdToAssessmentTypes.get(nextTargetUserId);
        if(coveredTypesForUser != null)
            allAssessmentTypes.removeAll(coveredTypesForUser);      
        
        AssessmentType chatAssessmentType = !allAssessmentTypes.isEmpty() ?
                                             allAssessmentTypes.get( Constants.RANDOM.nextInt(allAssessmentTypes.size()) ) :
                                             randomAssessmentType;
        
        return new ChatMetadata(chatterUserId, nextTargetUserId, nextTopic, chatAssessmentType);
    }
}