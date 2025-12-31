/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.service;

import com.parillume.model.CompanyModel;
import com.parillume.model.CorpusModel;
import com.parillume.model.external.Company;
import com.parillume.model.external.Role;
import com.parillume.model.external.Team;
import com.parillume.model.external.User;
import com.parillume.model.score.CSStrengthScore;
import com.parillume.model.score.EnneagramScore;
import com.parillume.model.score.MBTypeScore;
import com.parillume.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class ModelValidator {
    
    private Set<String> errors = new LinkedHashSet<>();
    
    private CorpusModel corpusModel;
    private List<CompanyModel> dbCompanies = new ArrayList<>();
        
    public boolean hasErrors() { return !errors.isEmpty(); }
    public Set<String> getErrors() { return errors; }
    
    public ModelValidator(CorpusModel corpusModel, List<CompanyModel> allCompanies) 
    throws Exception {
        setCorpusModel(corpusModel);
        setDbCompanies(allCompanies);
    }
    
    public void validateCompany(CompanyModel companyModel)
    throws Exception {        
        Company company = companyModel.getCompany();
        
        try {
           company.validateFields(companyModel);
        } catch(Exception exc) {
            errors.add(exc.getMessage());
        }
            
        for(Team team: companyModel.getTeams()) {
            try {
                team.validateFields(companyModel);
                validateTeamReferences(companyModel, team);
            } catch(Exception exc) {
                errors.add(exc.getMessage());
            }
        }
            
        for(Role role: companyModel.getRoles()) {
            try {
                role.validateFields(companyModel);
            } catch(Exception exc) {
                errors.add(exc.getMessage());
            }
        }
          
        validateUsers(companyModel, companyModel.getUsers());
    }
    
    public void validateUsers(CompanyModel companyModel, List<User> users) 
    throws Exception {
        for(User user: users) {
            try {
                user.validateFields(companyModel);
                validateUserReferences(companyModel, user);
            } catch(Exception exc) {
                errors.add(exc.getMessage());
            }
        }
        
        verifyUniqueUsernames(companyModel, users);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    private void verifyUniqueUsernames(CompanyModel companyModel, List<User> submittedUsers) 
    throws Exception {
        // Build a map of all future users:
        Map<String,User> idToUser = companyModel.getUsers()
                                                .stream()
                                                .collect(Collectors.toMap(u->u.getId(), u->u));
        for(User user: submittedUsers) {
            // A submitted user will overwrite its own entry from the DB:
            idToUser.put(user.getId(), user);
        }
        
        try {
            // Ensure that there are no username conflicts among all future users:
            Set<String> uniqueUsernames = new HashSet<>();
            for(User thisUser: idToUser.values()) {
                if( StringUtil.isEmpty(thisUser.getEmailAddress()) )
                    continue;
                
                if( !uniqueUsernames.add(thisUser.getEmailAddress()) ) {
                    throw new Exception("Email address " + thisUser.getEmailAddress() + " is already in use");
                }
            }

            // Ensure that there are no username conflicts with the company itself:
            if( !uniqueUsernames.add(companyModel.getCompany().getUsername()) ) {
                throw new Exception("Username " + companyModel.getCompany().getUsername() + " is already in use");
            }
        } catch(Exception exc) {
            errors.add(exc.getMessage());
        }
    }
    
    private void validateTeamReferences(CompanyModel companyModel, Team team) 
    throws Exception {
        if(!team.getCompanyIds().contains(companyModel.getCompany().getId()))
            errors.add("Team references invalid company id");        
    }
    
    private void validateUserReferences(CompanyModel companyToValidate, User user) 
    throws Exception {
        List<String> csIds = Arrays.asList(CSStrengthScore.values())
                                   .stream()
                                   .map(cs -> cs.getId())
                                   .collect(Collectors.toList());
        List<String> mbIds = Arrays.asList(MBTypeScore.values())
                                   .stream()
                                   .map(cs -> cs.getId())
                                   .collect(Collectors.toList());
        List<String> ennIds = Arrays.asList(EnneagramScore.values())
                                    .stream()
                                    .map(cs -> cs.getId())
                                    .collect(Collectors.toList());

        List<CompanyModel> allCompanies = new ArrayList(dbCompanies);
        // Add companyToValidate to the companies we assess. Although dbCompanies
        // may containcompanyToValidate, the latter may have new data.
        allCompanies.add(companyToValidate);
        
        //// ASSESSMENTS
        List<String> validAssessmentIds = new ArrayList<>();
        validAssessmentIds.addAll(csIds);
        validAssessmentIds.addAll(mbIds);
        validAssessmentIds.addAll(ennIds);     
        if( !validAssessmentIds.containsAll(user.getAssessmentResultIds()) ) {
            List<String> invalidIds = new ArrayList(user.getAssessmentResultIds());
            invalidIds.removeAll(validAssessmentIds);
            errors.add("User has invalid assessment ids: " + invalidIds);
        }
        
        //// USERS
        List<String> allUserIds = allCompanies.stream()
                                              .flatMap(c -> c.getUsers().stream().map(u -> u.getId()))
                                              .collect(Collectors.toList());
        
        Set<String> userManagerIds = new HashSet( user.getTeamIdToManagerId().values());   
        if( !allUserIds.containsAll(userManagerIds) ) {
            userManagerIds.removeAll(allUserIds);
            errors.add("User references to manager have invalid manager ids: " + userManagerIds);
        }

        //// MANAGERS
        List<String> allTeamIds = allCompanies.stream()
                                              .flatMap(c -> c.getTeams().stream().map(t -> t.getId()))
                                              .collect(Collectors.toList());      
        
        Set<String> userTeamIds = new HashSet( user.getTeamIdToManagerId().keySet() );
        if( !allTeamIds.containsAll(userTeamIds) ) {
            userTeamIds.removeAll(allTeamIds);
            errors.add("User references to manager have invalid team ids: " + userTeamIds);
        }
        
        List<String> teamIdsForManagers = new ArrayList(user.getTeamIdToManagerId().keySet());
        for(CompanyModel company: allCompanies) {
            List<String> teamIds = company.getTeams().stream().map(t->t.getId()).collect(Collectors.toList());  
            List<String> userIds = company.getUsers().stream().map(t->t.getId()).collect(Collectors.toList());  
            
            for(String managerTeamId: teamIdsForManagers) {
                if(teamIds.contains(managerTeamId)) {
                    String managerId = user.getTeamIdToManagerId().get(managerTeamId);
                    if(!userIds.contains(managerId)) {
                        errors.add("User manager id " + managerId + " does not correspond to team id " + managerTeamId);
                    }
                }
            }
        }         
        
        //// ROLES
        List<String> allRoleIds = allCompanies.stream()
                                              .flatMap(c -> c.getRoles().stream().map(r -> r.getId()))
                                              .collect(Collectors.toList());       
        allRoleIds.add(""); // "" is a valid role ID
        Set<String> userRoleIds = new HashSet( user.getTeamIdToRoleId().values());
        if( !allRoleIds.containsAll(userRoleIds) ) {
            userRoleIds.removeAll(allRoleIds);
            errors.add("User has invalid role ids: " + userRoleIds);
        }

        List<String> teamIdsForRoles = new ArrayList(user.getTeamIdToRoleId().keySet());
        for(CompanyModel company: allCompanies) {
            List<String> teamIds = company.getTeams().stream().map(t->t.getId()).collect(Collectors.toList());  
            List<String> roleIds = company.getRoles().stream().map(t->t.getId()).collect(Collectors.toList());  
            roleIds.add(""); // "" is a valid role id
            
            for(String roleTeamId: teamIdsForRoles) {
                if(teamIds.contains(roleTeamId)) {
                    String roleId = user.getTeamIdToRoleId().get(roleTeamId);
                    if(!roleIds.contains(roleId)) {
                        errors.add("User role id " + roleId + " does not correspond to team id " + roleTeamId);
                    }
                }
            }
        } 
    }    
}
