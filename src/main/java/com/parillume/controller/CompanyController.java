/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.controller;

import com.parillume.model.CompanyModel;
import com.parillume.model.external.Company;
import com.parillume.model.external.Team;
import com.parillume.model.external.User;
import com.parillume.security.Session;
import com.parillume.security.dto.SessionDTO;
import com.parillume.security.service.PermissionsService;
import com.parillume.security.service.SessionService;
import com.parillume.service.ModelValidatorService;
import com.parillume.util.Constants;
import com.parillume.util.CryptionUtil;
import com.parillume.util.StringUtil;
import com.parillume.webapp.dto.CredentialsDTO;
import com.parillume.webapp.dto.UpsertTeamsDTO;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
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
public class CompanyController extends AbstractController {  
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    ModelValidatorService validatorService;
    
    @Autowired
    private PermissionsService permissionsService;
    
    @Autowired
    private SecurityController securityController;
    
    @Autowired
    private EntityManager entityManager;

    @PostMapping(path = "/validatecompany", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> validateCompany(@RequestBody CompanyModel company,
                                                  @RequestParam(value = "corpusversion") String corpusVersion) {
        try {
            validatorService.validateCompany(corpusVersion, company);
            return createResponse("Company is valid");
        } catch(Exception exc) {
            return createResponse("Company is invalid", exc);
        }
    }

    /**
     * The keys of the teamsMap are defined in index.html: newteam, nameteam_[teamid], deleteteam_[teamid]
     */
    @PostMapping(path = "/upsertteams", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> upsertTeams(@RequestBody UpsertTeamsDTO upsertTeamsDTO,  
                                              @RequestParam(value = "companyid") String companyId,
                                              @RequestParam(value = "sessionid") String sessionId,
                                              @RequestParam(value = "corpusversion") String corpusVersion) {
        try {
            sessionService.validateSession(sessionId); 
            
            CompanyModel company = companyService.get(companyId);
            if(company == null)
                throw new Exception("Company " + companyId + " not found");
            
            // Add new team
            String newTeamName = upsertTeamsDTO.getNewTeamName();
            if( !StringUtil.isEmpty(newTeamName) ) {
                company.getTeams().add( new Team(newTeamName, companyId) );
            }
            
            // Update team names
            for(Team team: company.getTeams()) {
                if( upsertTeamsDTO.getTeamIdToName().containsKey(team.getId()) )
                    team.setName( upsertTeamsDTO.getTeamIdToName().get(team.getId()) );
            }
            
            // Enforce unique team names
            Set<String> existingNames = new HashSet<>();
            for(Team team: company.getTeams()) {
                if( !existingNames.add(team.getName()) )
                    throw new Exception("Team with name " + team.getName() + " already exists");
            }
                                                  
            // Delete specified teams from company
            company.getTeams().removeIf(t -> upsertTeamsDTO.getTeamIdsToDelete().contains(t.getId()) );
            
            // Remove references to deleted teams from users
            List<String> remainingTeamIds = company.getTeams()
                                                   .stream()
                                                   .map(t->t.getId())
                                                   .collect(Collectors.toList());
            company.getUsers().stream().forEach(
                u -> u.maintainTeams(remainingTeamIds)
            );
            
            // Persist to database
            companyService.update(company);
            
            return createResponse("Company " + company.getCompany().getName() + " updated");
        } catch(Exception exc) {
            return createResponse("Failed to edit teams", exc);
        }
    }
    
    @GetMapping(path = "/getcompany", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCompany(@RequestParam(value = "companyid") String companyId,
                                             @RequestParam(value = "sessionid") String sessionId) {
        try {     
            Session session = sessionService.validateSession(sessionId); 
            
            if(!StringUtil.nullEquals(session.getCompanyId(), companyId))
                permissionsService.verifyPermissions(session, "getcompany");
            // else: Everyone can get their own company
            
            CompanyModel company = retrieveCompany(companyId);
            if(company == null)
                throw new Exception("Company " + companyId + " not found");
            
            company.getCompany().setPassword(Constants.PASSWORD_MASK);
            company.getUsers().forEach(u -> u.setPassword(Constants.PASSWORD_MASK));
            
            return createObjectResponse(company);
            
        } catch(Exception exc) {            
            return createResponse("Failed to get company " + companyId, exc);
        }          
    }
    
    /**
     * Seed the database with the Parillume company
     * @return
     */
    @GetMapping(path = "/seedparillume") 
    public ResponseEntity<String> seedParillume() {
        String companyName = "Parillume";
        try {      
            Session session = Session.getParillumeSession();
            permissionsService.verifyPermissions(session, "seedparillume");           
            return createObjectResponse( makeCompany(companyName, session.getCompanyId(), 
                                                     session.getUsername(), session.getPassword(),
                                                     session) );            
        } catch(Exception exc) {            
            return createResponse("Failed to seed company " + companyName, exc);
        }           
    }
    
    @GetMapping(path = "/createcompany")
    public ResponseEntity<String> createCompany(@RequestParam(value = "companyname") String companyName,
                                                @RequestParam(value = "sessionid") String sessionId) {
        try {                 
            Session session = sessionService.validateSession(sessionId); 
            permissionsService.verifyPermissions(session, "createcompany");
            return createObjectResponse( makeCompany(companyName, StringUtil.createAlphanumericID(), 
                                                     null, null, session) );
        } catch(Exception exc) {            
            return createResponse("Failed to create company " + companyName, exc);
        }           
    }
    
    @PostMapping(path = "/addcompany", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addCompany(@RequestBody CompanyModel company,
                                             @RequestParam(value = "corpusversion") String corpusVersion,
                                             @RequestParam(value = "sessionid") String sessionId) {
        String newCompanyId = company.getCompany().getId();
        
        try {                 
            Session session = sessionService.validateSession(sessionId); 
            permissionsService.verifyPermissions(session, "addcompany");
            
            if(retrieveCompany(newCompanyId) != null)
                throw new Exception("Company with id="+newCompanyId+" already exists");
                
            validatorService.validateCompany(corpusVersion, company);
            
            companyService.add(company);
            return createObjectResponse(company);
        } catch(Exception exc) {            
            return createResponse("Failed to add company " + newCompanyId, exc);
        }   
    } 
    
    @PostMapping(path = "/updatecompany", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCompany(@RequestBody CompanyModel companyModel,
                                                @RequestParam(value = "corpusversion") String corpusVersion,
                                                @RequestParam(value = "sessionid") String sessionId) {
        String companyId = companyModel.getCompany().getId();
        
        try {
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "updatecompany");
        
            CompanyModel origModel = retrieveCompany(companyId);
            if(origModel == null)
                throw new Exception("Company with id="+companyId+" does not exist");
            
            validatorService.validateCompany(corpusVersion, companyModel);
            
            Company company = companyModel.getCompany();
            ////// Replace password masks
            if(StringUtil.nullEquals(Constants.PASSWORD_MASK, company.getPassword())) {
                // A masked password represents an unchanged password
                company.setPassword( origModel.getCompany().getPassword() );
            } else {
                company.setPassword( CryptionUtil.conditionallyDecryptGlobal(company.getPassword()) );
            }
            
            Map<String, String> userIdToOrigPassword = origModel.getUsers().stream()
                                                                  .collect(Collectors.toMap(u -> u.getId(), u -> u.getPassword())); 
            for(User user: companyModel.getUsers()) {
                if(StringUtil.nullEquals(Constants.PASSWORD_MASK, user.getPassword())) {
                    // A masked password represents an unchanged password
                    if(userIdToOrigPassword.containsKey(user.getId()))
                        user.setPassword(userIdToOrigPassword.get(user.getId()));
                    else
                        user.setPassword("");
                } else {
                    user.setPassword( CryptionUtil.conditionallyDecryptGlobal(user.getPassword()) );
                }
            }
            
            companyService.update(companyModel);
            
            return createObjectResponse(companyModel);
        } catch(Exception exc) {            
            return createResponse("Failed to update company " + companyId, exc);
        }   
    }
    
    @GetMapping(path = "/deletecompany")
    public ResponseEntity<String> deleteCompany(@RequestParam(value = "companyid") String companyId,
                                                @RequestParam(value = "sessionid") String sessionId) {
        try {
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "deletecompany"); 
        
            companyService.delete(companyId);
                        
            imageService.deleteCompanyImages(companyId);
            
            // Clear cached DB data
            entityManager.clear(); 
            
            CompanyModel companyModel = companyService.get(session.getCompanyId());
            Company company = companyModel.getCompany();
            CredentialsDTO credentials = new CredentialsDTO(company.getUsername(), company.getPassword());
            return createObjectResponse( new SessionDTO(session, credentials.getUsername(), company.getName(),
                                                        company.getId(),
                                                        securityController.getManagedCompanyReferences(credentials, companyModel)) );
        } catch(Exception exc) {            
            return createResponse("Failed to delete company " + companyId, exc);
        }  
    }    
    
    private SessionDTO makeCompany(String companyName, String companyId, 
                                   String username, String password, 
                                   Session session) throws Exception {
        if( companyService.getCompanies()
                          .stream()
                          .anyMatch(c -> StringUtil.nullEquals(c.getCompany().getName(), companyName)) ) {
            throw new Exception("Company with name " + companyName + " already exists");
        }

        Company newCompany = new Company();
        newCompany.setName(companyName);
        newCompany.setId(companyId);
        if(!StringUtil.isEmpty(username)) 
            newCompany.setUsername(username);
        if(!StringUtil.isEmpty(password)) 
            newCompany.setPassword(password);
        CompanyModel newCompanyModel = new CompanyModel();
        newCompanyModel.setCompany(newCompany);
        companyService.add(newCompanyModel);

        // Clear cached DB data
        entityManager.clear(); 

        CompanyModel companyModel = companyService.get(session.getCompanyId());
        Company company = companyModel.getCompany();
        CredentialsDTO credentials = new CredentialsDTO(company.getUsername(), company.getPassword());
        return new SessionDTO(session, credentials.getUsername(), company.getName(),
                              company.getId(),
                              securityController.getManagedCompanyReferences(credentials, companyModel));        
    }
}
