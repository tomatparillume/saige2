/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.controller;

import com.parillume.model.CompanyModel;
import com.parillume.model.CorpusModel;
import com.parillume.model.ModelWriter;
import com.parillume.model.external.Company;
import com.parillume.security.Session;
import com.parillume.security.dto.SessionDTO;
import com.parillume.security.service.PermissionsService;
import com.parillume.security.service.SessionService;
import com.parillume.util.StringUtil;
import com.parillume.webapp.dto.AssessmentResultsDTO;
import com.parillume.webapp.dto.AssessmentsDTO;
import com.parillume.webapp.dto.CredentialsDTO;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.tuple.Pair;
import static org.apache.tomcat.jni.User.username;
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
public class CorpusController extends AbstractController {
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private PermissionsService permissionsService;
    
    @Autowired
    private EntityManager entityManager;
    
    @GetMapping(path = "/getcorpus")
    public ResponseEntity<String> getCorpus(@RequestParam(value = "corpusversion") String corpusVersion,
                                            @RequestParam(value = "sessionid") String sessionId) 
    throws Exception {  
        try {
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "getcorpus");  
        
            return createObjectResponse(retrieveRequiredCorpus(corpusVersion) );
        } catch(Exception exc) {            
            return createResponse("Failed to get corpus " + corpusVersion, exc);
        }          
    }
    
    /**
     * Seed the database with a corpus
     * 
     * @param overwrite: Overwrite existing corpus in database? Default: false
     * @return 
     */
    @GetMapping(path = "/seedcorpus") 
    public ResponseEntity<String> seedCorpus(@RequestParam(value = "overwrite", required=false) Boolean overwrite) {
        try {      
            Session session = Session.getParillumeSession();;   
            permissionsService.verifyPermissions(session, "seedcorpus");    
                        
            Pair<CorpusModel, CompanyModel> models = ModelWriter.generateModels();
            CorpusModel corpusModel = models.getLeft();
            
            CorpusModel existingCorpus = corpusService.get(corpusModel.getVersion()); 
            if(existingCorpus != null) {
                if(overwrite==null || !overwrite) {
                    throw new Exception("Corpus with version " + corpusModel.getVersion() +
                                        " already exists; submit 'overwrite=true' to overwrite it");
                }
                corpusService.update(corpusModel);
                
            } else
                corpusService.add(corpusModel);
            
            // Clear cached DB data
            entityManager.clear(); 
        
            return createObjectResponse(corpusModel);
            
        } catch(Exception exc) {            
            return createResponse("Failed to seed corpus", exc);
        }           
    }
    
    @PostMapping(path = "/addcorpus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addCorpus(@RequestBody CorpusModel corpus,
                                            @RequestParam(value = "sessionid") String sessionId) {
        String version = corpus.getVersion();
        try {     
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "addcorpus");  
            
            if(corpusService.get(version) != null)
                throw new Exception("Corpus with version="+version+" already exists");
            
            corpusService.add(corpus);
            return createResponse("Corpus version " + version + " created");
        } catch(Exception exc) {            
            return createResponse("Failed to add corpus version " + version, exc);
        }   
    }
    
    @PostMapping(path = "/updatecorpus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCorpus(@RequestBody CorpusModel corpus,
                                               @RequestParam(value = "sessionid") String sessionId) {
        String version = corpus.getVersion();
        try {
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "updatecorpus");   
            
            retrieveRequiredCorpus(version); // Verify that corpus exists
            
            corpusService.update(corpus);
            return createResponse("Corpus version " + version + " updated");
        } catch(Exception exc) {            
            return createResponse("Failed to update corpus version " + version, exc);
        }   
    }
    
    @GetMapping(path = "/deletecorpus")
    public ResponseEntity<String> deleteCorpus(@RequestParam(value = "corpusversion") String corpusVersion,
                                               @RequestParam(value = "sessionid") String sessionId) {
        try {
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "deletecorpus");    
            
            corpusService.delete(corpusVersion);
            return createResponse("Corpus version " + corpusVersion + " deleted");
        } catch(Exception exc) {            
            return createResponse("Failed to delete corpus version " + corpusVersion, exc);
        }  
    }
    
    @GetMapping(path = "/getassessmenttypes")
    public ResponseEntity<String> getAssessmentTypes(@RequestParam(value = "corpusversion") String corpusVersion,
                                                     @RequestParam(value = "sessionid") String sessionId) 
    throws Exception {
        try {  
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "getassessmenttypes");  
               
            CorpusModel corpus = retrieveRequiredCorpus(corpusVersion);
            
            return createObjectResponse(corpus.getAssessments());
        } catch(Exception exc) {            
            return createResponse("Failed to get assessment types with corpus version " + corpusVersion, exc);
        }          
    }   
    
    @GetMapping(path = "/getassessmentresults")
    public ResponseEntity<String> getAssessmentResults(@RequestParam(value = "corpusversion") String corpusVersion,
                                                       @RequestParam(value = "sessionid") String sessionId) {
        try {     
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "getassessmentresults"); 
            
            CorpusModel corpus = retrieveRequiredCorpus(corpusVersion); 
            
            return createMapResponse(new AssessmentResultsDTO(corpus));
        } catch(Exception exc) {            
            return createResponse("Failed to get assessments for corpus version " + corpusVersion, exc);
        }              
    }
    
    /**
     * Returns a map of key=[assessment id] and value=[AssessmentResult JSON]
     * for a given AssessmentType
     */
    @GetMapping(path = "/getassessments")
    public ResponseEntity<String> getAssessments(
                @RequestParam(value = "corpusversion") String corpusVersion,
                @RequestParam(value = "sessionid") String sessionId) {
        try {     
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "getassessments"); 
            
            CorpusModel corpus = retrieveRequiredCorpus(corpusVersion); 
            
            return createObjectResponse(new AssessmentsDTO(corpus) );
        } catch(Exception exc) {            
            return createResponse("Failed to get assessments for corpus version " + corpusVersion, exc);
        }         
    }
}
