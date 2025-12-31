/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.controller;

import com.parillume.log.LogRecord;
import com.parillume.log.service.LogService;
import com.parillume.print.input.WorksheetDataImporter;
import com.parillume.security.Session;
import com.parillume.security.service.PermissionsService;
import com.parillume.security.service.SessionService;
import com.parillume.util.FileUtil;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@RestController
public class LogsController extends AbstractController {
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private PermissionsService permissionsService;
    
    @Autowired
    private LogService logService;
    
    @Autowired
    private EntityManager entityManager; 
    
    @GetMapping(path = "/getlogs")
    public ResponseEntity<String> getLogs(@RequestParam(value = "sessionid") String sessionId) {
        try {    
            Session session = sessionService.validateSession(sessionId);   
            permissionsService.verifyPermissions(session, "getlogs");  

            List<LogRecord> logRecords = logService.getLogs();
            
            return createObjectResponse(logRecords);
            
        } catch(Exception exc) {            
            return createResponse("Failed to get logs", exc);
        }          
    } 
}
