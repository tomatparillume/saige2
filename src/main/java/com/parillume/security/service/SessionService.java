/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.security.service;

import com.parillume.error.ExpiredSessionException;
import com.parillume.model.CompanyModel;
import com.parillume.security.Session;
import com.parillume.security.repository.SessionRepository;
import com.parillume.service.CompanyService;
import com.parillume.util.StringUtil;
import com.parillume.webapp.dto.CredentialsDTO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Service
@Data
public class SessionService {
    
    @Autowired 
    private CompanyService companyService;
    
    /**
     * Creates and validates a new Session
     */
    public Session createSession(String username, String password, String companyId,
                                 boolean userCredentials) 
    throws Exception {
        return createSession(new Session(username, password, companyId, userCredentials));
    }
    public Session createSession(Session session) 
    throws Exception {
        synchronized(SessionService.class) {
            String sessionId = session.toSessionID();
            
            SessionRepository.put(sessionId);

            try {
                validateSession(sessionId);
            } catch(Exception exc) {
                expireSession(sessionId);
                throw exc;
            }

            return session;
        }
    }
    
    public void updateSession(Session origSession, CredentialsDTO newCredentials) throws Exception {      
        synchronized(SessionService.class) {
            String origSessionId = origSession.toSessionID();
            origSession.setUsername(newCredentials.getUsername());
            origSession.setPassword(newCredentials.getPassword());  
            expireSession(origSessionId);
            createSession(origSession);                     
        }
    }
    
    public void extendSession(String sessionId) throws Exception {
        if(!SessionRepository.has(sessionId))
            throw new ExpiredSessionException();
        
        SessionRepository.put(sessionId, System.currentTimeMillis());
    }
    
    public void expireSession(String sessionId) {
        SessionRepository.remove(sessionId);
    }
    
    public void validateSession(String sessionId, String companyId) throws Exception {  
        Session session = validateSession(sessionId);   
        
        if(!StringUtil.nullEquals(companyId, session.getCompanyId()))
            throw new Exception("Session is not valid for company " + companyId);        
    }
    
    public Session validateSession(String sessionId) throws Exception {      
        Long timestamp = SessionRepository.getTimestamp(sessionId);
        if(timestamp == null)
            throw new ExpiredSessionException();
        
        Session session = new Session(sessionId);
        
        CompanyModel model = getCompanyService().get(session.getCompanyId());
        if(model == null)
            throw new Exception("Company " + session.getCompanyId() + " not found");
        
        session.validate(model, timestamp);
        extendSession(sessionId);
        
        return session;
    }
}