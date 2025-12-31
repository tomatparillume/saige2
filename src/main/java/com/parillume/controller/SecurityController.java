/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.controller;

import com.parillume.error.ExpiredSessionException;
import com.parillume.model.CompanyModel;
import com.parillume.model.external.Company;
import com.parillume.model.external.User;
import com.parillume.security.PermissibleAction;
import com.parillume.security.Session;
import com.parillume.security.dto.SessionDTO;
import com.parillume.security.service.PermissionsService;
import com.parillume.security.service.SessionService;
import com.parillume.service.CompanyService;
import com.parillume.service.ModelValidatorService;
import com.parillume.service.UserService;
import com.parillume.util.Constants;
import com.parillume.util.StringUtil;
import com.parillume.webapp.dto.CredentialsDTO;
import com.parillume.webapp.dto.LoginDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@RestController
public class SecurityController extends AbstractController {
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PermissionsService permissionsService;
    
    @Autowired
    ModelValidatorService validatorService;
    
    @Autowired
    private EntityManager entityManager;
    
    @GetMapping(path = "/validatesession")
    public ResponseEntity<String> validateSession(@RequestParam(value = "sessionid", required=false) String sessionId) {
        if(StringUtil.isEmpty(sessionId))
            return createResponse(Boolean.FALSE.toString());
        
        try {
            sessionService.validateSession(sessionId);
            return createResponse(Boolean.TRUE.toString());
        } catch(Exception exc) {
            return createResponse(Boolean.FALSE.toString());
        }
    }
    
    @GetMapping(path = "/extendsession")
    public ResponseEntity<String> extendSession(@RequestParam(value = "sessionid", required=false) String sessionId) {
        
        try {
            if(StringUtil.isEmpty(sessionId))
                throw new Exception("No session ID was provided");
            
            sessionService.extendSession(sessionId);
            return createResponse("Session was extended");
            
        } catch(ExpiredSessionException exc) {
            return createResponse("Session expired; please log in again", exc);
            
        } catch(Exception exc) {
            return createResponse("Failed to extend session", exc);
        }
    }
    
    @PostMapping(path = "/changecompanycredentials", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changeCompanyCredentials(@RequestBody CredentialsDTO credentials,
                                                           @RequestParam(value = "companyid") String companyId,
                                                           @RequestParam(value = "corpusversion") String corpusVersion,
                                                           @RequestParam(value = "sessionid") String sessionId) {
        try {
            if(StringUtil.isEmpty(credentials.getUsername()))
                throw new Exception("Please provide an email address/username");
            
            if( !StringUtil.isValidEmail(credentials.getUsername()) ) 
                throw new Exception("Username is not a valid email address");
                
            Session session = sessionService.validateSession(sessionId);
            if(!StringUtil.nullEquals(session.getCompanyId(), companyId))
                permissionsService.verifyPermissions(session, "changecompanycredentials");
        
            CompanyModel companyModel = retrieveCompany(companyId);
            if(companyModel == null)
                throw new Exception("Company with id="+companyId+" does not exist");
            
            Company company = companyModel.getCompany();
            
            company.setUsername(credentials.getUsername());
            if(!StringUtil.isEmpty(credentials.getPassword()))
                company.setPassword(credentials.getPassword());    
            
            companyService.update(companyModel);            

            // If the calling Admin is editing their own company, this endpoint
            // changed their creds - and thus their UI session. Update the session:
            if(StringUtil.nullEquals(session.getCompanyId(), companyId)) {
                // Clear the cached company, so the newly-upserted company creds are visible
                entityManager.clear(); 
                sessionService.updateSession(session, new CredentialsDTO(company.getUsername(), company.getPassword()));                              
                return createObjectResponse( new SessionDTO(session, company.getUsername(), company.getName(),
                                                            company.getId(),
                                                            getManagedCompanyReferences(credentials, companyModel)) );              
            } else {
                return createResponse(""); // Indicates that the session hasn't been updated
            }
        } catch(Exception exc) {            
            return createResponse("Failed to update password for company " + companyId, exc);
        }         
    }       
    
    /**
    @PostMapping(path = "/updatepassword")
    public ResponseEntity<String> updatePassword(@RequestBody String newPassword,
                                                 @RequestParam(value = "username") String username,
                                                 @RequestParam(value = "corpusversion") String corpusVersion,
                                                 @RequestParam(value = "sessionid") String sessionId) 
    throws Exception {
        try {
            sessionService.validateSession(sessionId);

            Session session = new Session(sessionId);
            
            if( !StringUtil.nullEquals(session.getUsername(), username) ) {
                throw new Exception("Username is invalid");
            }
            
            Pair<CompanyModel,User> pair = userService.get(username, session.getPassword());
            if(pair == null) {
                throw new Exception("Team member " + username + " could not be found");
            }
            
            CompanyModel companyModel = pair.getLeft();
            User user = pair.getRight();
            
            user.setPassword(newPassword);
            userService.upsertUsers(companyModel.getCompany().getId(), corpusVersion, Arrays.asList(user));
            
            session.setPassword(newPassword);
            
            Map<String,String> managedCompanyReferences = getManagedCompanyReferences(username, companyModel);
            
            return createObjectResponse( new SessionDTO(session, username, user.getId(), companyModel.getCompany().getName(),
                                                        managedCompanyReferences) );            
            
        } catch(Exception exc) {            
            return createResponse("Failed to update password", exc);
        }  
    }
    */
    
    @GetMapping(path = "/proxylogin")
    public ResponseEntity<String> proxyLogin(@RequestParam(value = "targetuserid") String targetUserId,
                                             @RequestParam(value = "sessionid") String sessionId) {        
        try {
            Session callerSession = sessionService.validateSession(sessionId); 
            if(callerSession.isUserCredentials())
                throw new Exception("Only administrators can log in as a proxy user");
            
            SessionDTO callerSessionDTO = getSessionDTO( 
                                            new CredentialsDTO(callerSession.getUsername(), callerSession.getPassword()) 
                                          );
            
            User user = userService.getUser(callerSession.getUsername(), callerSession.getPassword());
            if(user == null)
                throw new Exception("User not found");
            user.setPassword(Constants.PASSWORD_MASK);
            
            LoginDTO callerLoginDTO = new LoginDTO(callerSessionDTO, user);
            
            LoginDTO targetLoginDTO = null;
            for(CompanyModel companyModel: companyService.getCompanies()) {
                Optional<User> opt = companyModel.getUsers()
                                                 .stream()
                                                 .filter(u -> StringUtil.nullEquals(targetUserId, u.getId()))
                                                 .findFirst();
                if(opt.isPresent()) {
                    User targetUser = opt.get();
                    
                    Session targetSession = sessionService.createSession(targetUser.getEmailAddress(), 
                                                                         targetUser.getPassword(),
                                                                         companyModel.getCompany().getId(), 
                                                                         true);
                    SessionDTO sessionDTO = new SessionDTO(targetSession, 
                                                           targetUser.getEmailAddress(), 
                                                           companyModel.getCompany().getName(),
                                                           targetUserId,
                                                           null);
                    targetLoginDTO = new LoginDTO(sessionDTO, targetUser);
                    
                    targetUser.setPassword(Constants.PASSWORD_MASK);
                    break;
                }
            }
            if(targetLoginDTO == null)
                throw new Exception("Proxy user not found");
            
            List<LoginDTO> retList = new ArrayList<>(
                                        Arrays.asList(callerLoginDTO, targetLoginDTO)
                                     );
            return createObjectResponse(retList);            
        } catch(Exception exc) {            
            return createResponse("Failed to log in as proxy user " + targetUserId, exc);
        } 
    }
    
    /**
     * username and password could be for a company or for a particular user
     * Returns a sessionId
     */
    @PostMapping(path = "/login")
    public ResponseEntity<String> logIn(@RequestBody CredentialsDTO credentials) 
    throws Exception {
        try {
            SessionDTO sessionDTO = getSessionDTO(credentials);            
            User user = userService.getUser(credentials.getUsername(), credentials.getPassword());
            
            return createObjectResponse( new LoginDTO(sessionDTO, user) );            
        } catch(Exception exc) {            
            return createResponse("Failed to log in", exc);
        }  
    }
    

    @GetMapping(path = "/logout")
    public ResponseEntity<String> logOut(@RequestParam(value = "sessionid") String sessionId) throws Exception {
        try {
            sessionService.expireSession(sessionId);
            return createResponse(sessionId + " logged out");
        } catch(Exception exc) {            
            return createResponse("Failed to log out", exc);
        }  
    }    
    
    /**
     * Returns {companyId : [company username, company name]}
     */
    public Map<String,Pair<String,String>> getManagedCompanyReferences(CredentialsDTO credentials, CompanyModel companyModel) {
        Map<String,Pair<String,String>> managedCompanyReferences = new HashMap<>();
            
        Company company = companyModel.getCompany();
        List<PermissibleAction> permissibleActions = permissionsService.getPermissibleActions(credentials.getUsername(), company.getId());
            
        // Admins with ALL permissions (i.e. Parillume admins) can manage all companies:
        if(permissibleActions.contains(PermissibleAction.ALL)) {
            managedCompanyReferences.putAll( companyService.getCompanyReferences() );

        } else if( StringUtil.nullEquals(credentials.getUsername(), company.getUsername()) &&
                   StringUtil.nullEquals(credentials.getPassword(), company.getPassword())
                 ) {
            // The logged-in user represents their own company:
            managedCompanyReferences.put(company.getId(), MutablePair.of(company.getUsername(),company.getName())); 
            
            List<String> managedCompanyIds = companyModel.getManagedCompanyIds();
            managedCompanyReferences.putAll(
                companyService.getCompanyReferences()
                    .entrySet()
                    .stream()
                    .filter( e -> managedCompanyIds.contains(e.getKey()) )
                    .collect(Collectors.toMap(e->e.getKey(), e->e.getValue()))
            );
        } 
        
        return managedCompanyReferences;
    }
    
    private SessionDTO getSessionDTO(CredentialsDTO credentials) throws Exception {
        if(!credentials.isPopulated())
            throw new Exception("Please provide credentials");

        boolean userCredentials = false;
        String actorId = null; // company id or user id

        String username = credentials.getUsername();
        String password = credentials.getPassword();

        // Does username/password correspond to company-level credentials?
        CompanyModel companyModel = companyService.get(username, password);
        if(companyModel != null) {
            actorId = companyModel.getCompany().getId();
        } else {
            // Does username/password correspond to a user in a company?
            Pair<CompanyModel,User> pair = userService.get(username, password);                
            if(pair != null) {
                userCredentials = true;
                companyModel = pair.getLeft();
                actorId = pair.getRight().getId();
            }
        }

        if(companyModel == null)
            throw new Exception("No company is associated with submitted username and password");

        Company company = companyModel.getCompany();
        Session session = sessionService.createSession(username, password, company.getId(), userCredentials);

        Map<String,Pair<String,String>> managedCompanyReferences = getManagedCompanyReferences(credentials, companyModel);                    
        return new SessionDTO(session, username, company.getName(),
                                                 actorId, // company id or user id
                                                 managedCompanyReferences);
    }
}
