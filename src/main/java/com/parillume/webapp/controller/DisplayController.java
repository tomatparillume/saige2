/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.controller;

import com.parillume.controller.AbstractController;
import com.fasterxml.jackson.databind.JsonNode;
import com.parillume.model.CompanyModel;
import com.parillume.model.CorpusModel;
import com.parillume.model.score.CSStrengthScore;
import com.parillume.model.score.EnneagramScore;
import com.parillume.model.score.MBTypeScore;
import com.parillume.print.display.DiskImage;
import com.parillume.security.PermissibleAction;
import com.parillume.security.Session;
import com.parillume.security.service.PermissionsService;
import com.parillume.security.service.SessionService;
import com.parillume.util.StringUtil;
import com.parillume.webapp.dto.CompanyDTO;
import com.parillume.webapp.dto.ModelGlossaryDTO;
import com.parillume.webapp.service.DisplayService;
import java.util.Base64;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.util.IOUtils;
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
public class DisplayController extends AbstractController {
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private DisplayService displayService;
    
    @Autowired
    private PermissionsService permissionsService;
    
    @GetMapping(path = "/getmodelglossary")
    public ResponseEntity<String> getModelGlossary(@RequestParam(value = "companyid") String companyId,
                                                   @RequestParam(value = "sessionid") String sessionId) {
        try {
            ModelGlossaryDTO dto = new ModelGlossaryDTO(getCompanyService().get(companyId),
                                                        getCompanyService().getCompanies());
            return createObjectResponse(dto);
        } catch(Exception exc) {            
            return createResponse("Failed to get model glossary for company " + companyId, exc);
        }               
    }
    
    @GetMapping(path = "/getimagebytes")
    public ResponseEntity<String> getImageBytes(@RequestParam(value = "imageid", required=false) String imageId) {
        try {
            DiskImage image = DiskImage.getImage(imageId);
            byte[] byteArray = IOUtils.toByteArray(image.getImageStream()); 
            return createResponse( Base64.getEncoder().encodeToString(byteArray) );
        } catch(Exception exc) {            
            return createResponse("Failed to image " + imageId, exc);
        }   
    }
    
    /**
     * Currently unused
    @GetMapping(path = "/getusers")
    public ResponseEntity<String> getUsers(@RequestParam(value = "companyid", required=false) String companyId,
                                           @RequestParam(value = "sessionid") String sessionId,
                                           // If not submitted, sessionId's company will be used:
                                           @RequestParam(value = "managedcompanyid", required = false) String managedCompanyId) 
    throws Exception {
        try {
            CompanyModel company = getCompany(sessionId, companyId, managedCompanyId, "getusers");
            return createObjectResponse( displayService.getUsers(company) );
        } catch(Exception exc) {            
            return createResponse("Failed to get users for company " + companyId, exc);
        }     
    }
    */
    
    @GetMapping(path = "/getcompanydto")
    public ResponseEntity<String> getCompanyDTO(@RequestParam(value = "companyid", required=false) String companyId,
                                                @RequestParam(value = "sessionid") String sessionId,
                                                // If not submitted, sessionId's company will be used:
                                                @RequestParam(value = "managedcompanyid", required = false) String managedCompanyId) 
    throws Exception {
        try {
            CompanyModel companyModel = getCompany(sessionId, companyId, managedCompanyId, "getcompanydto");
            return createObjectResponse(new CompanyDTO(companyModel));
        } catch(Exception exc) {            
            return createResponse("Failed to get company DTO for company " + companyId, exc);
        }     
    }
    
    /**
     * This method requires EITHER users OR assessments to be specified in the teamFiltersNode
     */
    @PostMapping(path = "/generatefilteredtable", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateFilteredTable(@RequestBody JsonNode teamFiltersNode, 
                                                        @RequestParam(value = "corpusversion") String corpusVersion,
                                                        @RequestParam(value = "companyid", required=false) String companyId,
                                                        @RequestParam(value = "columns") List<String> columnIds,
                                                        // true by default; false==assessments filter
                                                        @RequestParam(value = "usersfilter", required=false) Boolean usersFilter,
                                                        @RequestParam(value = "sessionid") String sessionId) 
    throws Exception {
        try {
            if(usersFilter == null)
                usersFilter = true;

            Set<String> userIds = null;
            Set<String> csResultIds = null;
            Set<String> mbResultIds = null;
            Set<String> ennResultIds = null;

            if(usersFilter) {
                // Populate userIds from the submitted teamFiltersNode
                userIds = new HashSet<>();
                parseTeamFiltersNode(teamFiltersNode, userIds, null, null, null);
            } else {
                // Populate result ids from the submitted teamFiltersNode
                csResultIds = new HashSet<>();
                mbResultIds = new HashSet<>();
                ennResultIds = new HashSet<>();
                parseTeamFiltersNode(teamFiltersNode, null, csResultIds, mbResultIds, ennResultIds);

                // Populate ALL user ids
                userIds = companyService.get(companyId).getUsers()
                                                       .stream()
                                                       .map(u -> u.getId())
                                                       .collect(Collectors.toSet());
            }

            return generateTable(sessionId, corpusVersion, companyId, null,
                                 columnIds, userIds, csResultIds, mbResultIds, ennResultIds);  
        } catch(Exception exc) {            
            return createResponse("Failed to generate table for company " + companyId, exc);
        }         
    }

    private ResponseEntity<String> generateTable(String sessionId, String corpusVersion, String companyId, 
                                                 Boolean orOperator,
                                                 List<String> columnIds,
                                                 Set<String> userIds,
                                                 Set<String> csResultIds,
                                                 Set<String> mbResultIds,
                                                 Set<String> ennResultIds) throws Exception {
        try {           
            Session session = sessionService.validateSession(sessionId);  
            
            if(StringUtil.isEmpty(companyId))
                companyId = getCompanyId(sessionId); 

            CompanyModel company = retrieveCompany(companyId);
            if(company == null)
                throw new Exception("Company with id "+companyId+" does not exist");

            CorpusModel corpus = retrieveRequiredCorpus(corpusVersion);
            if(corpus == null)
                throw new Exception("Corpus with version " +corpusVersion+" does not exist");
            
            if(!StringUtil.nullEquals(getCompanyId(sessionId), companyId))
                permissionsService.verifyPermissions(session, "generatecompanytable");
            // else: Everyone can generate their own company table
            
            if(orOperator == null)
                orOperator = true;           
            
            if(orOperator) {
                company.getUsers().removeIf(u -> 
                    !userIds.contains(u.getId()) ||
                    // If resultIds are defined. user must have at least one requested resultId                       
                    !( (csResultIds == null || StringUtil.collectionsIntersect(u.getAssessmentResultIds(),csResultIds)) ||
                       (mbResultIds == null || StringUtil.collectionsIntersect(u.getAssessmentResultIds(),mbResultIds)) ||
                       (ennResultIds == null || StringUtil.collectionsIntersect(u.getAssessmentResultIds(),ennResultIds)) 
                     ) );
            } else { // AND operator
                company.getUsers().removeIf(u -> 
                    !userIds.contains(u.getId()) ||
                    // User must have all requested assessment results                        
                    !( u.getAssessmentResultIds().containsAll(csResultIds) &&
                       u.getAssessmentResultIds().containsAll(mbResultIds) &&
                       u.getAssessmentResultIds().containsAll(ennResultIds) 
                     ) );              
            }
            
            return createObjectResponse( displayService.generateTable(company, corpus, columnIds) );
        } catch(Exception exc) {            
            return createResponse("Failed to generate table for company " + companyId, exc);
        }   
    } 
    
    private CompanyModel getCompany(String sessionId, String companyId, String managedCompanyId,
                                    String endpoint) 
    throws Exception {
        Session session = sessionService.validateSession(sessionId);

        if(StringUtil.isEmpty(companyId))
            companyId = getCompanyId(sessionId); 

        if(!StringUtil.nullEquals(getCompanyId(sessionId), companyId))
            permissionsService.verifyPermissions(session, endpoint);
        // else: Everyone can get their own company's data

        if(!StringUtil.isEmpty(managedCompanyId) && !StringUtil.nullEquals(companyId, managedCompanyId)) {
            CompanyModel managerCompanyModel = companyService.get(session.getCompanyId());
            List<String> managedCompanyIds = managerCompanyModel.getManagedCompanyIds();

            if( managedCompanyIds.contains(managedCompanyId) ||
                permissionsService.getPermissibleActions(session.getUsername(), companyId)
                                  .contains(PermissibleAction.ALL) ) {
                companyId = managedCompanyId;
            } else {
               throw new Exception("Company username " + session.getUsername() + 
                                   " is not allowed to manage company " + managedCompanyId); 
            }
        }

        CompanyModel company = retrieveCompany(companyId);
        if(company == null)
            throw new Exception("Company with id " + companyId + " does not exist");     
        
        return company;
    }
    
    private void parseTeamFiltersNode(JsonNode teamFiltersNode,
                                      Set<String> userIds,
                                      Set<String> csResultIds,
                                      Set<String> mbResultIds,
                                      Set<String> ennResultIds) {
        for(Iterator<JsonNode> iter = teamFiltersNode.elements(); iter.hasNext();) {
            JsonNode childNode = iter.next();
            if(!childNode.has("name") || !childNode.has("value"))
                continue;

            if( userIds != null && childNode.get("name").textValue().startsWith("userid") )
                userIds.add(childNode.get("value").textValue());

            if( csResultIds != null && mbResultIds != null && ennResultIds != null &&
                StringUtil.nullEquals("assessmentresultid", childNode.get("name").textValue()) ) {
                String resultId = childNode.get("value").textValue();

                CSStrengthScore strength = CSStrengthScore.getStrengthById(resultId);
                if(strength != null) csResultIds.add(resultId);

                MBTypeScore mb = MBTypeScore.getScoreById(resultId);
                if(mb != null) mbResultIds.add(resultId);   

                EnneagramScore enn = EnneagramScore.getScoreById(resultId);
                if(enn != null) ennResultIds.add(resultId);                   
            }
        }        
    }
}
