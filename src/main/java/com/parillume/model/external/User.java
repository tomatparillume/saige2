/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.parillume.model.CompanyModel;
import com.parillume.model.internal.Entity;
import com.parillume.util.*;
import com.parillume.util.model.CalendarType;
import com.parillume.util.model.EntityType;
import com.parillume.util.model.EmailSchedule;
import com.parillume.webapp.dto.CredentialsDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 *  * @author tom@parillume.com
 */
@Data
@JsonPropertyOrder({ "id", "entityType", "nameFirst", "nameLast", "emailAddress", "phoneNumber", "isActive", "calendarType", "emailSchedule"})
public class User implements Entity {
    private String id;
    
    private String nameFirst = "";
    private String nameLast = "";
    private String emailAddress = ""; // Also servers as login username
    private String phoneNumber;
    
    private String password = "";
    
    private boolean isActive = true;
    
    // The history of percent-in-superpowers, in ascending time order
    private TreeMap<Long,Integer> percentageInSuperpowersHistory = new TreeMap<>();
    
    /**
     * User IDs of other users who canNOT be targets (subjects) in this user's
     * auto-generated chat emails.
     * By default - if this list is empty - no users are excluded.
     * ChatGPTService will remove obsolete users from this list when a chat is
     * generated for this user.
     */
    private List<String> excludedChatUserIds = new ArrayList<>();
    
    private CalendarType calendarType = CalendarType.GOOGLE;
    private EmailSchedule emailSchedule = EmailSchedule.WEEKDAYS;
    
    private Map<String,String> teamIdToRoleId = new HashMap<>();
    private Map<String,String> teamIdToManagerId = new HashMap<>();
    
    private List<String> assessmentResultIds = new ArrayList<>();    
    
    /**
     * CUSTOMIZED: Not used by Saige
     * These lists are customized phrases; e.g. "I am great with cheese", "I am scared of rocks"
     */
    private List<String> superpowers = new ArrayList<>();
    private List<String> kryptonite = new ArrayList<>();
    
    /**
     * These list ids of assessment results that have been selected to 
     * represent the User's chosen sup/kryp.
     */
    private List<String> superpowerAssessmentResultIds = new ArrayList<>();
    private List<String> kryptoniteAssessmentResultIds = new ArrayList<>();
    
    private List<String> playfullPractices = new ArrayList<>();
    
    public User() {
        setId(StringUtil.createAlphanumericID());
    }
    
    public User(String emailAddress) {
        this();
        setEmailAddress(emailAddress);
    }
    
    public User(String firstName, String lastName) {
        this();
        setNameFirst(firstName);
        setNameLast(lastName);
    }
    
    public User(CredentialsDTO credentials) {
        this();
        setEmailAddress(credentials.getUsername());
        setPassword(credentials.getPassword());
    }
    
    /************************ IGNORE LEGACY ATTRIBUTES *************************/
    @JsonIgnore
    public Integer getPercentageSuperpowers() {return null;}
    @JsonIgnore
    public void setPercentageSuperpowers(Integer i) {}
    /***************************************************************************/

    /**
     * Remove references to all teams whose ids are not in the submitted list
     */
    public void maintainTeams(List<String> teamIds) {
        getTeamIdToManagerId().keySet().removeIf(key -> !teamIds.contains(key));
        getTeamIdToRoleId()   .keySet().removeIf(key -> !teamIds.contains(key));
    }
    
    @JsonIgnore
    public String getFirstLastName() {
        return getNameFirst() + " " + getNameLast();
    }
    
    @JsonIgnore
    public Integer getLatestPercentageSuperpowers() {
        TreeMap<Long,Integer> sorted = getPercentageInSuperpowersHistory();
        return !sorted.isEmpty() ? 
                sorted.lastEntry().getValue() :
                null;
    }
    public void appendLatestPercentageSuperpowers(Integer percentage) {
        synchronized(this) {
            if(percentage != null) {
                // Remove any other entries from today; we only keep one entry per day.
                long startOfDayMs = TimeUtil.getStartOfDayMs(0);
                getPercentageInSuperpowersHistory().entrySet().removeIf(e -> e.getKey() >= startOfDayMs);
                
                // Remove entries older than 2 years old
                long twoYearsAgo = TimeUtil.getStartOfDayMs(365 *2);
                getPercentageInSuperpowersHistory().entrySet().removeIf(e -> e.getKey() < twoYearsAgo);
                
                getPercentageInSuperpowersHistory().put(System.currentTimeMillis(), percentage);
            }
        }
    }
    
    public User clone() {
        User clone = new User();
// NO; email address serves as username: 
//        clone.setEmailAddress(getEmailAddress());
        clone.setActive(isActive());
        clone.setNameFirst(getNameFirst());
        clone.setNameLast(getNameLast());
        clone.setPhoneNumber(getPhoneNumber());
        clone.setCalendarType(getCalendarType());
        clone.setEmailSchedule(getEmailSchedule());
        clone.setAssessmentResultIds(new ArrayList(getAssessmentResultIds()));
        clone.setSuperpowerAssessmentResultIds(new ArrayList(getSuperpowerAssessmentResultIds()));
        clone.setKryptoniteAssessmentResultIds(new ArrayList(getKryptoniteAssessmentResultIds()));
        clone.setSuperpowers(new ArrayList(getSuperpowers()));
        clone.setKryptonite(new ArrayList(getKryptonite()));
        clone.setPlayfullPractices(new ArrayList(getPlayfullPractices()));
        clone.setTeamIdToRoleId(getTeamIdToRoleId().entrySet()
                                                   .stream()
                                                   .collect(Collectors.toMap(e->e.getKey(), e->e.getValue()))
                               );
        clone.setTeamIdToManagerId(getTeamIdToManagerId().entrySet()
                                                         .stream()
                                                         .collect(Collectors.toMap(e->e.getKey(), e->e.getValue()))
                               );
        return clone;
    }    
    
    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.USER;
    }
    
    @Override
    public void validateFields(CompanyModel companyModel) throws Exception {
        List<String> missingFields = new ArrayList<>();
        
        if(StringUtil.isEmpty(getId()))
            missingFields.add("id");
        if(StringUtil.isEmpty(getNameFirst()))
            missingFields.add("first name");
        if(StringUtil.isEmpty(getNameLast()))
            missingFields.add("last name");
        
        // If credentialsRequired:
//        if(StringUtil.isEmpty(getPassword()))
//            missingFields.add("password");
        
        String invalidEmail = null;
//        if(StringUtil.isEmpty(getEmailAddress())) {
//            missingFields.add("email address");
//        } else if(!StringUtil.isValidEmail(getEmailAddress())) {
//            invalidEmail = "Email address is invalid";
//        }
        
        Set<String> invalidTeamIds = new HashSet<>();
        List<String> companyTeamIds = companyModel.getTeams()
                                                  .stream()
                                                  .map(t -> t.getId())
                                                  .collect(Collectors.toList());
        Set<String> managerTeamIds = new HashSet(getTeamIdToManagerId().keySet());
        managerTeamIds.removeAll(companyTeamIds);
        invalidTeamIds.addAll(managerTeamIds);
        
        Set<String> roleTeamIds = new HashSet(getTeamIdToRoleId().keySet());
        roleTeamIds.removeAll(companyTeamIds);
        invalidTeamIds.addAll(roleTeamIds);
        
        String error = null;
        if(!missingFields.isEmpty()) {
            error = "User is missing required fields: " + missingFields;
        }
        if(!invalidTeamIds.isEmpty()) {
            String msg = "Referenced team ids are not associated with user's company: " +
                          invalidTeamIds;
            error = (error == null) ?
                     msg :
                     error + "\n" + msg;
        }
        if(invalidEmail != null) {
            error = (error == null) ?
                     invalidEmail :
                     error + "\n" + invalidEmail;
        }
        
        if(error != null)
            throw new Exception(error);
    }
    
    public void addRole(String teamId, String roleId) {
        getTeamIdToRoleId().put(teamId, roleId);
    }
    
    public void removeRole(String roleId) {
        getTeamIdToRoleId().entrySet().removeIf(e -> StringUtil.nullEquals(e.getValue(), roleId));
    }
    
    public void addManager(String teamId, String managerID) {
        getTeamIdToManagerId().put(teamId, managerID);
    }
    
    public void removeManager(String managerID) {
        getTeamIdToManagerId().entrySet().removeIf(e -> StringUtil.nullEquals(e.getValue(), managerID));
    }

    public Map<String, String> getTeamIdToRoleId() {
        if(!getTeamIdToManagerId().isEmpty()) {
            // If this user has managers for some teams but no roles for those
            // teams, set the user's role to "" on each of those teams.
            Set<String> managedTeamIds = new HashSet( getTeamIdToManagerId().keySet() );
            managedTeamIds.removeAll(teamIdToRoleId.keySet());
            for(String managedTeamId: managedTeamIds) {
                teamIdToRoleId.put(managedTeamId, "");
            }
        }
        return teamIdToRoleId;
    }   
}
