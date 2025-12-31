/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.controller;

import com.parillume.error.InvalidPermissionsException;
import com.parillume.model.CompanyModel;
import com.parillume.model.external.Company;
import com.parillume.model.external.User;
import com.parillume.print.input.WorksheetDataImporter;
import com.parillume.security.Session;
import com.parillume.security.dto.SessionDTO;
import com.parillume.security.service.PermissionsService;
import com.parillume.security.service.SessionService;
import com.parillume.service.ModelValidatorService;
import com.parillume.util.FileUtil;
import com.parillume.service.UserService;
import com.parillume.util.webapp.UserUpdater;
import com.parillume.webapp.dto.CredentialsDTO;
import com.parillume.webapp.dto.UpdatedUserDTO;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@RestController
public class UserController extends AbstractController {
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    ModelValidatorService validatorService;
    
    @Autowired
    private PermissionsService permissionsService;
    
    @Autowired
    private EntityManager entityManager; 
    
    @PostMapping(path = "/validateuser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validateUser(@RequestBody User user,
                                               @RequestParam(value = "companyid") String companyId,
                                               @RequestParam(value = "corpusversion") String corpusVersion) {
        try {
            validatorService.validateUser(corpusVersion, companyId, user);
            return createResponse("User is valid");
            
        } catch(Exception exc) {
            return createResponse("User is invalid", exc);
        }
    }
    
    /**
     * Import users from worksheets in the /tmp directory into the DB.
     */
    @GetMapping(path = "/importusers")
    public ResponseEntity<String> importUsers(@RequestParam(value = "companyid") String companyId,
                                              @RequestParam(value = "sessionid") String sessionId) {
        try {    
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "importusers");  

            StringBuffer warningContainer = new StringBuffer();
            WorksheetDataImporter importer = new WorksheetDataImporter(FileUtil.getTempWorksheets());
            userService.upsertUsersFromWorksheets(companyId, importer, warningContainer); 
        
            String response = "Users imported for company "+companyId;
            if(warningContainer.length()>0)
                response += "\n" + warningContainer;
            
            return createResponse(response);
            
        } catch(Exception exc) {            
            return createResponse("Failed to import users for company " + companyId, exc);
        }          
    } 
    
    /**
     * Uploads worksheet file
     */
    @PostMapping("/uploadusers")
    public ResponseEntity<String> uploadUser(@RequestParam("files") List<MultipartFile> files,
                                             @RequestParam(value = "companyid") String companyId,
                                             @RequestParam(value = "sessionid") String sessionId) {
        try {
            Map<File,InputStream> worksheetFiles = new HashMap<>();
            for(MultipartFile file: files) {
                worksheetFiles.put(new File(file.getOriginalFilename()), file.getInputStream());
            }
            
            StringBuffer warningContainer = new StringBuffer();
            WorksheetDataImporter importer = new WorksheetDataImporter(worksheetFiles);
            CompanyModel companyModel = userService.upsertUsersFromWorksheets(companyId, importer, warningContainer); 
        
            String response = "User(s) uploaded for company "+companyModel.getCompany().getName();
            if(warningContainer.length()>0)
                response += "\n" + warningContainer;
            
            return createResponse(response);
            
        } catch(Exception exc) {            
            return createResponse("Failed to uploaded users for company " + companyId, exc);
        }          
    }
    
    /**
     * Update a user's own profile.
     * Each Map contains ("name":"somename", "value":"somevalue"} submitted from
     * the index.html:selfeditform
     */
    @PostMapping(path = "/updateself")
    public ResponseEntity<String> updateSelf(@RequestBody List<Map<String,String>> userProperties,
                                             @RequestParam(value = "companyid") String companyId,
                                             @RequestParam(value = "corpusversion") String corpusVersion,
                                             @RequestParam(value = "sessionid") String sessionId)  {
        try {
            Session session = sessionService.validateSession(sessionId);   
            
            User user = null;
            try {
                Pair<CompanyModel,User> pair  = userService.get(session.getUsername(), session.getPassword());
                user = pair.getRight();
                permissionsService.verifyPermissions(session, "updateself");  
            } catch(InvalidPermissionsException ipe) {
                if( !verifySelfEditingPermissions(session, user) )
                    throw ipe;
                // else: Users can edit themselves
            }
            
            CompanyModel companyModel = companyService.get(companyId);
            
            UserUpdater updateDTO = new UserUpdater(user, userProperties, corpusService.get(corpusVersion), companyModel);
            boolean credentialsUpdated = updateDTO.update(user);
            
            validatorService.validateUser(corpusVersion, companyId, user);
            
            userService.upsertUsers(companyId, Arrays.asList(user));
            
            if(credentialsUpdated) {
                // Clear the cached user, so the newly-upserted user's password is visible
                entityManager.clear(); 
                sessionService.updateSession(session, new CredentialsDTO(user.getEmailAddress(),user.getPassword()));
            }
                    
            Company company = companyModel.getCompany();    
            SessionDTO sessionDTO = new SessionDTO(session, session.getUsername(), 
                                                   company.getName(), company.getId(),
                                                   null);
            return createObjectResponse(new UpdatedUserDTO(sessionDTO, user));
        } catch(Exception exc) {            
            return createResponse("Failed to update user for company " + companyId, exc);
        }  
    }
    
    /**
     * Add or update users in the referenced company.
     */
    @PostMapping(path = "/upsertusers")
    public ResponseEntity<String> upsertUsers(@RequestBody List<User> users,
                                              @RequestParam(value = "companyid") String companyId,
                                              @RequestParam(value = "corpusversion") String corpusVersion,
                                              @RequestParam(value = "sessionid") String sessionId)  {
        try {
            Session session = sessionService.validateSession(sessionId);   
            
            try {
                permissionsService.verifyPermissions(session, "upsertusers");  
            } catch(InvalidPermissionsException ipe) {
                if(!verifySelfEditingPermissions(session, users))
                    throw ipe;
                // else: Users can edit themselves
            }
            
            validatorService.validateUsers(corpusVersion, companyId, users);
            
            userService.upsertUsers(companyId, users);
            return createResponse("Users upserted for company "+companyId+" and corpus version "+corpusVersion);
        } catch(Exception exc) {  
            return createResponse("Failed to upsert users for company " + companyId, exc);
        }  
    }
    
    /**
     * Delete users in the referenced company.
     */
    @GetMapping(path = "/deleteusers")
    public ResponseEntity<String> deleteUsers(@RequestParam(value = "userid") List<String> userIds,
                                              @RequestParam(value = "companyid") String companyId,
                                              @RequestParam(value = "sessionid") String sessionId) {
        try {
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "deleteusers"); 
            
            userService.deleteUsers(companyId, userIds);
            return createResponse("Users deleted for company " + companyId);
        } catch(Exception exc) {            
            return createResponse("Failed to delete users for company " + companyId, exc);
        }          
    }
        
    @GetMapping(path = "/getnewusertemplate")
    public ResponseEntity<String> getNewUserJSONTemplate() {
        try {
            return createObjectResponse( userService.getNewUserJSONTemplate() );
        } catch(Exception exc) {            
            return createResponse("Failed to get new user JSON template", exc);
        }          
    }
    
    private boolean verifySelfEditingPermissions(Session session, List<User> users) 
    throws Exception {
        return verifySelfEditingPermissions(session, users.toArray(new User[0]));
    }
    private boolean verifySelfEditingPermissions(Session session, User... users) 
    throws Exception {
        boolean verified = false;
        
        if(users.length > 1)
            return verified; // A User cannot edit multiple other Users

        CompanyModel companyModel = userService.getCompany(session.getCompanyId());
        try {
            session.verifyCredentials(companyModel);
            verified = true;
        } catch(Exception exc) {}
        
        return verified;
    }
}
