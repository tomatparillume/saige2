/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.util.webapp;

import com.parillume.model.CompanyModel;
import com.parillume.model.CorpusModel;
import com.parillume.model.external.Team;
import com.parillume.model.external.User;
import com.parillume.model.internal.AssessmentResult;
import com.parillume.model.lexicon.Term;
import com.parillume.model.score.CSStrengthScore;
import com.parillume.model.score.EnneagramScore;
import com.parillume.model.score.MBTypeScore;
import com.parillume.util.Constants;
import com.parillume.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Used by the UserController.updateSelf endpoint.
 *
 *  * @author tom@parillume.com
 */
@Data
public class UserUpdater {
    // Each map contains ("name":"somename", "value":"somevalue"}
    private List<Map<String,String>> userProperties = new ArrayList<>();
    
    private User user;
    private CorpusModel corpusModel;
    private CompanyModel companyModel;
    
    public UserUpdater(User user, List<Map<String,String>> userProperties, 
                       CorpusModel corpusModel, CompanyModel companyModel) {
        setUser(user);
        setUserProperties(userProperties);
        setCorpusModel(corpusModel);
        setCompanyModel(companyModel);
    }
    
    /**
     * Returns true if password was updated
     */
    public boolean update(User userToUpdate) throws Exception {
        String firstName = getFirstName();
        if(!StringUtil.isEmpty(firstName))
            userToUpdate.setNameFirst(firstName);
        
        String lastName = getLastName();
        if(!StringUtil.isEmpty(lastName))
            userToUpdate.setNameLast(lastName);
        
        String email = getEmailAddress();
        boolean emailChanged = false;
        if(!StringUtil.isEmpty(email)) {
            emailChanged = !StringUtil.nullEquals(userToUpdate.getEmailAddress(), email);
            userToUpdate.setEmailAddress(email);
        }
        
        String phone = getPhone();
        if(!StringUtil.isEmpty(phone))
            userToUpdate.setPhoneNumber(phone);
        
        List<String> includedChatUserIds = getIncludedChatUserIds();
        List<String> excludedChatUserIds = getCompanyModel().getUsers()
                                                            .stream()
                                                            .filter( u -> !includedChatUserIds.contains(u.getId()) )
                                                            .map(u -> u.getId())
                                                            .collect(Collectors.toList());
        // The user being updated should not have their own ID in their list of
        // excluded IDs - not because doing so will break chat generation, but
        // because doing so (1) takes up DB space, and (2) persists a confusing
        // and misleading "my own id" value as an excluded ID.
        excludedChatUserIds.remove(getUser().getId());
        
        userToUpdate.setExcludedChatUserIds(excludedChatUserIds);
        
        userToUpdate.setPlayfullPractices(getPFPs());
        userToUpdate.setAssessmentResultIds(getAssessmentResultIds());
        
        userToUpdate.appendLatestPercentageSuperpowers(getLatestPercentageSuperpowers());
        
        Pair<Map<String,String>, Map<String,String>> pair = getSuperpowersAndKryptonite();
        Map<String,String> superpowers = pair.getLeft();
        Map<String,String> kryptonite = pair.getRight();
        
        userToUpdate.setSuperpowerAssessmentResultIds( new ArrayList(superpowers.keySet()) );
        userToUpdate.setKryptoniteAssessmentResultIds( new ArrayList(kryptonite.keySet()) );
        
        userToUpdate.setSuperpowers( new ArrayList(superpowers.values()) );
        userToUpdate.setKryptonite( new ArrayList(kryptonite.values()) );
        
        Map<String,String> newTeamIdToRoleId = new HashMap<>();
        for(Team team: companyModel.getTeams()) {
            String roleId = getValue(team.getId());
            if(!StringUtil.isEmpty(roleId)) {
                newTeamIdToRoleId.put(team.getId(), !StringUtil.nullEquals(Constants.NOROLE_ID, roleId) ?
                                                    roleId : "");
            }
        }
        userToUpdate.setTeamIdToRoleId(newTeamIdToRoleId);
                
        
        boolean credsChanged = emailChanged;        
        String newPassword = getNewPassword();
        if(!StringUtil.isEmpty(newPassword)) {
            userToUpdate.setPassword(newPassword);
            credsChanged = true;
        }
        
        return credsChanged;
    }
    
    // The keys (e.g. "firstname") are defined in index.html:selfeditform
    public String getFirstName() { return getValue("firstname"); }
    public String getLastName() { return getValue("lastname"); }
    public String getEmailAddress() { return getValue("email"); }
    public String getPhone() { return getValue("phone"); }
    public Integer getLatestPercentageSuperpowers() { 
        String percentage = getValue("percentagesup"); 
        return !StringUtil.isEmpty(percentage) ?
                Integer.valueOf(percentage) :
                null;
    }
    public List<String> getIncludedChatUserIds() {
        return getValues("userid_chattargets");
    }
    public String getNewPassword() throws Exception {
        String newPassword1 = getValue("newpassword1");
        String newPassword2 = getValue("newpassword2");
        
        boolean hasNewPassword = !StringUtil.isEmpty(newPassword1) || !StringUtil.isEmpty(newPassword2);
        if( hasNewPassword && !StringUtil.nullEquals(newPassword1, newPassword2)) {
            throw new Exception("New passwords do not match");
        }
        return hasNewPassword ? newPassword1 : null;
    }
    public List<String> getPFPs() throws Exception { 
        List<String> list = new ArrayList<>();

        String pfps = getValue("playfullpractices"); 
        if( !StringUtil.isEmpty(pfps) )
            list.addAll( Arrays.asList(pfps.split("\\r?\\n")) );

        if(list.size() > 7)
            throw new Exception("No more than 7 Play-full Practices are allowed");
        
        return list;       
    }
    /**
     * Pair< Map<assessmentIDs of superpowers, superpower Strings>, 
     *       Map<assessmentIDs of kryptonite, kryptonite Strings> >
     */
    public Pair<Map<String,String>, Map<String,String>> getSuperpowersAndKryptonite()  {
        Term superpowerTerm = getCorpusModel().getTerms().stream()
                                              .filter(t -> StringUtil.nullEquals(Constants.SUPERPOWER_TERMID, t.getId()))
                                              .findFirst()
                                              .get();
        Term kryptoniteTerm = getCorpusModel().getTerms().stream()
                                              .filter(t -> StringUtil.nullEquals(Constants.KRYPTONITE_TERMID, t.getId()))
                                              .findFirst()
                                              .get();
        
        Map<String,String> superpowers = new HashMap<>();
        Map<String,String> kryptonite = new HashMap<>();
        for(AssessmentResult result: getCorpusModel().getAssessmentResults()) {
            String label = " (" + result.getLabel() + ")";
            
            if( StringUtil.nullEquals("on", getValue("sup-" + result.getId())) ) {
                superpowers.put(result.getId(), result.getTermValue(superpowerTerm) + label);
            } 
            if( StringUtil.nullEquals("on", getValue("kryp-" + result.getId())) ) {
                kryptonite.put(result.getId(), result.getTermValue(kryptoniteTerm) + label );
            }
        }
        
        // Sort maps by value:
        superpowers = sortMapByValue(superpowers);
        kryptonite = sortMapByValue(kryptonite);
        
        return MutablePair.of(superpowers, kryptonite);
    }
    
    public List<String> getAssessmentResultIds() {
        List<String> ids = new ArrayList<>();
        
        ////// MB
        List<String> mbScoreIds = new ArrayList( Arrays.asList(MBTypeScore.values())
                                                       .stream()
                                                       .map(s -> s.getId())
                                                       .collect(Collectors.toList()) );
        List<Map<String,String>> mbKeyValues = getAssessmentResultsMap(mbScoreIds, false);   
        ids.addAll( mbKeyValues.stream().map(m -> m.get("name")).collect(Collectors.toList()) );
        
        ////// EN
        List<String> enScoreIds = new ArrayList( Arrays.asList(EnneagramScore.values())
                                                       .stream()
                                                       .map(s -> s.getId())
                                                       .collect(Collectors.toList()) ); 
        List<Map<String,String>> enKeyValues = getAssessmentResultsMap(enScoreIds, false); 
        ids.addAll( enKeyValues.stream().map(m -> m.get("name")).collect(Collectors.toList()) );

        ////// CS
        List<String> csScoreIds = new ArrayList( Arrays.asList(CSStrengthScore.values())
                                                       .stream()
                                                       .map(s -> s.getId())
                                                       .collect(Collectors.toList()) );
        List<Map<String,String>> csKeyValues = getAssessmentResultsMap(csScoreIds, true);    
        // Order csKeyValues from 1 to 5
        Collections.sort(csKeyValues, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> mA, Map<String, String> mB) {
                Integer orderA = Integer.valueOf( mA.get("value"));
                Integer orderB = Integer.valueOf( mB.get("value") );
                return Integer.compare(orderA, orderB);
            }
        });
        ids.addAll( csKeyValues.stream().map(m -> m.get("name")).collect(Collectors.toList()) );
        
        return ids;
    }
    
    private List<Map<String,String>> getAssessmentResultsMap(List<String> allScoreIds, boolean requireValue) {
        List<Map<String,String>> map = new ArrayList<>();
        
        for(Map<String,String> propsMap: userProperties) {
            String name = propsMap.get("name");
            String value = propsMap.get("value");
            if( allScoreIds.contains(name) &&
                (!requireValue || !StringUtil.isEmpty(value))
            ) {
                map.add(propsMap);
            }
        }
        return map;
    }
    
    private String getValue(String key) {
        List<String> values = getValues(key);
        return !values.isEmpty() ? values.get(0) : null;
    }
    private List<String> getValues(String key) {
        List<String> values = new ArrayList<>();
        for(Map<String,String> map: userProperties) {
            if( StringUtil.nullEquals(map.get("name"), key) ) {
                values.add( map.get("value") );
            }
        }
        return values;
    }
    
    private Map<String,String> sortMapByValue(Map<String,String> map) {
        return map.entrySet()
                  .stream()
                  .sorted(Map.Entry.comparingByValue())
                  .collect(Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue,
                                             (oldValue, newValue) -> oldValue, LinkedHashMap::new));        
    }
}