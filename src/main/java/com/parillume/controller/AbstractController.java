/*
 * Copyright(c) 2023 Parillume, All rights reserved worldwide
 */
package com.parillume.controller;

import com.parillume.error.ExpiredSessionException;
import com.parillume.error.InvalidPermissionsException;
import com.parillume.error.UnloggedExceptionIF;
import com.parillume.log.LogRecord;
import com.parillume.log.service.LogService;
import com.parillume.model.CompanyModel;
import com.parillume.model.CorpusModel;
import com.parillume.security.Session;
import com.parillume.util.StringUtil;
import com.parillume.service.CompanyService;
import com.parillume.service.CorpusService;
import com.parillume.service.ImageService;
import com.parillume.util.JSONUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public abstract class AbstractController {
    
    @Autowired
    protected CorpusService corpusService;
    
    @Autowired
    protected CompanyService companyService; 
    
    @Autowired
    protected ImageService imageService;   
    
    @Autowired
    protected LogService logService;  
       
    protected String getCompanyId(String sessionId) {
        if(StringUtil.isEmpty(sessionId))
            return null;
        
        try {
            return new Session(sessionId).getCompanyId();
        } catch(Exception exc) {
            return null;
        }
    }   
    
    protected CompanyModel retrieveCompany(String companyId) throws Exception {
        return companyService.get(companyId);
    }

    protected CorpusModel retrieveRequiredCorpus(String corpusVersion) throws Exception {
        CorpusModel corpus = corpusService.get(corpusVersion);
        if(corpus == null)
            throw new Exception("Corpus version " + corpusVersion + " not found"); 
        return corpus;
    }
         
    protected ResponseEntity<String> createMapResponse(Object o) throws Exception {
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("message", o);
        return new ResponseEntity<>(JSONUtil.toJSONNoEscape(retMap), HttpStatus.OK);
    }
    
    protected ResponseEntity<String> createObjectResponse(Object o) throws Exception {
        if(o instanceof JSONObject)
            return new ResponseEntity<>(o.toString(), HttpStatus.OK);
            
        JSONObject json = new JSONObject();
        json.put("message", o instanceof Collection?
                            JSONUtil.toJSONArray(o) :
                            JSONUtil.toJSONObject(o));
        return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    }
    
    protected ResponseEntity<String> createResponse(String message) {
        JSONObject json = new JSONObject();
        try {
            json.put("message", message);
        } catch(Exception exc) {
            System.out.println("Failed to build response '"+message+"': " + exc.getMessage());
        }
        return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    }
    
    protected ResponseEntity<String> createResponse(String error, Exception exc) {
        String msg = exc.getMessage(); 

        if( !(exc instanceof UnloggedExceptionIF) ) {
            if(!StringUtil.isEmpty(error))
                msg = error + ": " + msg;
                
            logService.log( new LogRecord(exc, msg) );
        }
        
        return new ResponseEntity<>("{\"error\":\""+msg+"\"}", HttpStatus.BAD_REQUEST);   
    }    
}