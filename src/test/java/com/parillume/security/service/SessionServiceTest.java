/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.security.service;

import com.parillume.model.CompanyModel;
import com.parillume.model.external.Company;
import com.parillume.security.Session;
import com.parillume.security.repository.SessionRepository;
import com.parillume.service.CompanyService;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class SessionServiceTest {
    private CompanyService companyService;
    private CompanyModel companyModel;
    
    private SessionService sessionService;
    
    private Map<String,Long> origSessions = new HashMap<>();
    
    private void init() throws Exception {
        origSessions.putAll( SessionRepository.getAll() );
        SessionRepository.getAll().clear();
        
        sessionService = new SessionService();
        
        companyService = mock(CompanyService.class);

        sessionService.setCompanyService(companyService);
         
        companyModel = new CompanyModel();
        companyModel.setCompany(new Company());
        
        companyModel.getCompany().setUsername("a");
        companyModel.getCompany().setPassword("b");
        companyModel.getCompany().setId("c");
        when(companyService.get("c")).thenReturn(companyModel);
    }
    
    @After
    public void tearDown() {
        SessionRepository.setAll(origSessions);
    }
    
    @Test
    public void testCreate() throws Exception {
        init(); 
        
        Session session = sessionService.createSession("a", "b", "c", false);
        
        Map<String,Long> map = SessionRepository.getAll();
        assertEquals(1, map.size());
        
        String sessionId = map.keySet().stream().findFirst().get();
        assertEquals(session.toSessionID(), sessionId);
        
        try {
            session = sessionService.createSession("aa", "b", "c", false);
            fail("Invalid username erroneously accepted");
        } catch(Exception exc) {}
        
        try {
            session = sessionService.createSession("a", "bb", "c", false);
            fail("Invalid password erroneously accepted");
        } catch(Exception exc) {}
        
        try {
            session = sessionService.createSession("a", "b", "cc", false);
            fail("Invalid company ID erroneously accepted");
        } catch(Exception exc) {}
    }
    
    @Test
    public void testValidate() throws Exception {
        init(); 
        
        Session session = sessionService.createSession("a", "b", "c", false);
        String sessionId = session.toSessionID();
        
        sessionService.validateSession(sessionId);
        
        SessionRepository.getAll().put(sessionId, 1000L);
        try {
            sessionService.validateSession(sessionId);
            fail("Expired timestamp erroneously validated");
        } catch(Exception exc) {}
        
        try {
            sessionService.validateSession("DUMMY");
            fail("Invalid session ID erroneously validated");
        } catch(Exception exc) {}    
        
        try {
            sessionId = sessionService.createSession("aa", "b", "c", false).toSessionID();
            sessionService.validateSession(sessionId);
            fail("Invalid username erroneously validated");
        } catch(Exception exc) {}  
        
        try {
            sessionId = sessionService.createSession("a", "bb", "c", false).toSessionID();
            sessionService.validateSession(sessionId);
            fail("Invalid password erroneously validated");
        } catch(Exception exc) {} 
        
        try {
            sessionId = sessionService.createSession("a", "b", "cc", false).toSessionID();
            sessionService.validateSession(sessionId);
            fail("Invalid company ID erroneously validated");
        } catch(Exception exc) {}     
    }    
    
    @Test
    public void testExpire() throws Exception {
        init();
        
        Session session = sessionService.createSession("a", "b", "c", false);
        String sessionId = session.toSessionID();
        
        sessionService.expireSession(sessionId);
        
        try {
            sessionService.validateSession(sessionId);
            fail("Expired ession erroneously validated");
        } catch(Exception exc) {}
    }
}
