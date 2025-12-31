/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.service;

import org.springframework.stereotype.Service;
import com.parillume.model.CompanyModel;
import com.parillume.model.external.User;
import com.parillume.model.score.CSStrengthScore;
import com.parillume.model.score.EnneagramScore;
import com.parillume.model.score.MBPreferenceScore;
import com.parillume.model.score.MBTypeScore;
import com.parillume.print.input.DataImportKeyIF;
import com.parillume.print.input.WorksheetDataImporter;
import com.parillume.util.Constants;
import com.parillume.util.CryptionUtil;
import com.parillume.util.JSONUtil;
import com.parillume.util.StringUtil;
import com.parillume.webapp.dto.CredentialsDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Service
public class UserService extends AbstractService {
    
    public JSONObject getNewUserJSONTemplate() throws Exception {        
        String template = JSONUtil.toJSON(new User());
        
        List<String> coreKeys = Arrays.asList("nameFirst", "nameLast", "emailAddress",
                                              "password", "active");
        JSONObject jsonObject = new JSONObject(template);
        for (Iterator<String> keys = jsonObject.keys(); keys.hasNext(); ) {
            String key = keys.next();
            if(!coreKeys.contains(key))
                keys.remove();
        }
        
        return jsonObject;
    }
    
    public CompanyModel upsertUsers(String companyId, List<User> users) 
    throws Exception {
        CompanyModel company = getCompany(companyId);
        return upsertUsers(company, users);
    }
    public CompanyModel upsertUsers(CompanyModel company, List<User> users) 
    throws Exception {
        List<User> companyUsers = company.getUsers();
        
        Map<String, User> idToUser = companyUsers.stream()
                                                 .collect(Collectors.toMap(u -> u.getId(), u -> u));     
        for(User user: users) {
            User prexistingUser = idToUser.get(user.getId());
            if(prexistingUser != null) {
                company.removeUser(user.getId());
                
                // If the user was submitted with a PASSWORD_MASK as their password,
                // then we maintain any preexisting password for this user:
                if( StringUtil.nullEquals(Constants.PASSWORD_MASK, user.getPassword()) ) 
                    user.setPassword( prexistingUser.getPassword() );                
            }            
            
            company.getUsers().add(user);
        }
        
        companyService.update(company);
        return company;
    }
    
    public CompanyModel deleteUsers(String companyId, List<String> userIds) 
    throws Exception {
        CompanyModel company = getCompany(companyId);
        for(String userId: userIds) {
            company.removeUser(userId);
        }
        
        companyService.update(company);
        return company;            
    }
    
    public User get(String userId) throws Exception {
        for(CompanyModel company: companyService.getCompanies()) {
            Optional<User> opt = company.getUsers()
                                        .stream()
                                        .filter( u -> StringUtil.nullEquals(userId, u.getId()) )
                                        .findFirst();
            if(opt.isPresent())
                return opt.get();
        }
        return null;
    }  

    /**
     * May return a skeleton user representing a company
     */
    public User getUser(String username, String password) {
        Pair<CompanyModel,User> pair = get(username, password);
        return pair != null ? pair.getRight() : null;
    }
    /**
     * May return a skeleton user representing a company
     */
    public Pair<CompanyModel,User> get(String username, String password) {
        for(CompanyModel company: companyService.getCompanies()) {            
            // If the username/password represent a company rather than a user,
            // return a skeleton user representing this company
            if( StringUtil.nullEquals( username, company.getCompany().getUsername() ) &&
                StringUtil.nullEquals( password, CryptionUtil.conditionallyDecryptGlobal(company.getCompany().getPassword()) )
            ) {
                return MutablePair.of( company, new User(new CredentialsDTO(username,password)) );
            }
            
            Optional<User> opt = company.getUsers()
                                        .stream()
                                        .filter( u -> StringUtil.nullEquals(username, u.getEmailAddress()) &&
                                                      StringUtil.nullEquals(password, CryptionUtil.conditionallyDecryptGlobal(u.getPassword())) )
                                        .findFirst();
            if(opt.isPresent())
                return MutablePair.of(company, opt.get());
        }
        
        return null;
    }
        
    public CompanyModel upsertUsersFromWorksheets(String companyId, 
                                                  WorksheetDataImporter importer,
                                                  StringBuffer warningContainer)
    throws Exception {
        CompanyModel companyModel = getCompany(companyId);
        List<User> companyUsers = companyModel.getUsers();
        
        Map<String, User> idToUser = companyUsers.stream()
                                                 .collect(Collectors.toMap(u -> u.getId(), u -> u));       
        
        // Collect all emails from the target company
        List<String> existingEmails = companyUsers.stream()
                                                  .filter(u -> !StringUtil.isEmpty(u.getEmailAddress()))
                                                  .map(u -> u.getEmailAddress())
                                                  .collect(Collectors.toList());
        
        // Collect all emails from other companies
        existingEmails.addAll( companyService.getCompanies()
                                             .stream()
                                             // Skip the target company
                                             .filter(cm -> !StringUtil.nullEquals(cm.getCompany().getId(), companyId))
                                             .flatMap(c -> c.getUsers().stream())
                                             .map(u -> u.getEmailAddress())
                                             .collect(Collectors.toList())
        );
        
        List<String> emailCollisions = new ArrayList<>();
        List<String> invalidFieldMessages = new ArrayList<>();
        List<User> usersToAdd = new ArrayList<>();
        for(DataImportKeyIF key: importer.getKeys()) {
            try {
                ////// Define User
                User user = null;
                
                String userId = importer.getUserId().get(key); 
                boolean existingUser = idToUser.containsKey(userId);
                
                String email = importer.getEmailAddress().get(key);                
                if(StringUtil.isEmpty(userId)) {
                    if(!StringUtil.isEmpty(email) && existingEmails.contains(email)) {
                        emailCollisions.add(email);
                        continue;
                    }                    
                    user = new User();
                    
                } else if(existingUser) {
                    user = idToUser.get(userId);
                    
                } else {
                    throw new Exception("User with id="+userId+" not found");
                }
                
                user.setNameFirst(importer.getFirstName(key));
                user.setNameLast(importer.getLastName(key));
                
                if(!StringUtil.isEmpty(email))
                    user.setEmailAddress(email);
                
                String password = importer.getPassword().get(key);
                if(!StringUtil.isEmpty(password))
                    user.setPassword(password);
                                          
                
                ////// Define User attributes  
                Integer enneagramType = importer.getEnneagram().get(key);    
                EnneagramScore ennScore = EnneagramScore.getScoreByType(enneagramType);
                user.getAssessmentResultIds().add(ennScore.getId());
                /*** Adopt all superpowers/kryptonite from all assessment scores ***/
                user.getSuperpowerAssessmentResultIds().add(ennScore.getId());
                user.getKryptoniteAssessmentResultIds().add(ennScore.getId());
                
                List<MBPreferenceScore> mbPrefs = importer.getPreferences().get(key);
                MBTypeScore mbType = MBTypeScore.getType(mbPrefs);
                user.getAssessmentResultIds().add(mbType.getId());
                /*** Adopt all superpowers/kryptonite from all assessment scores ***/
                user.getSuperpowerAssessmentResultIds().add(mbType.getId());
                user.getKryptoniteAssessmentResultIds().add(mbType.getId());
                
                List<CSStrengthScore> strengths = importer.getStrengths().get(key);
                for(CSStrengthScore csScore: strengths) {
                    user.getAssessmentResultIds().add(csScore.getId());
                    /*** Adopt all superpowers/kryptonite from all assessment scores ***/
                    user.getSuperpowerAssessmentResultIds().add(csScore.getId());
                    user.getKryptoniteAssessmentResultIds().add(csScore.getId());
                }
                
                // These are CUSTOMIZED superpowers/kryptonite, not used by Saige:
                List<String> superpowers = importer.getSuperpowers().get(key);
                user.setSuperpowers(superpowers);
                
                List<String> kryptonite = importer.getKryptonite().get(key);
                user.setKryptonite(kryptonite);
                
                List<String> pfps = importer.getPFPs().get(key);                
                user.setPlayfullPractices(pfps);
                
                try {
                    user.validateFields(companyModel);
                } catch(Exception e) {
                    invalidFieldMessages.add(e.getMessage());
                }
                
                if(!existingUser)
                    usersToAdd.add(user);
                
            } catch(Exception exc) {
                throw new Exception("Failed to upsert user from worksheet " + 
                                    key.getName() + ": " + exc.getMessage());
            }        
        }
        
        if(!emailCollisions.isEmpty()) {
            String msg = "User import(s) failed - imported email addresses already exist in the system: " + 
                         emailCollisions;
            if(emailCollisions.size() == importer.getKeys().size()) {
                throw new Exception(msg);
            } else {
                warningContainer.append(msg);
                warningContainer.append("\n");
            }
        }
        
        if(!invalidFieldMessages.isEmpty()) {
            String msg = "User(s) have invalid fields: " + invalidFieldMessages;
            if(invalidFieldMessages.size() == importer.getKeys().size())
                throw new Exception(msg);
            else
                warningContainer.append(msg);
        }
        
        for(User userToAdd: usersToAdd) {            
            companyUsers.add(userToAdd);
        }
        
        companyService.update(companyModel);
        
        return companyModel;
    }
}
