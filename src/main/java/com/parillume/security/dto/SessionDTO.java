/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.security.dto;

import com.parillume.security.Session;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;


/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class SessionDTO {
    private String sessionId;
    private String companyId;
    private String companyName;
    private String username;
    private String actorId;
    private boolean userCredentials = false;
    // {companyId, [company username, company name]} :
    private Map<String,Pair<String,String>> managedCompanyReferences = new HashMap<>();
    
    public SessionDTO() {}
        
    public SessionDTO(Session session, String username, String companyName,
                      // actorId: The company id or the user id, depending on who is logged in
                      String actorId, 
                      // {companyId, [company username, company name]} :
                      Map<String,Pair<String,String>> managedCompanyReferences) {
        setUsername(username);
        setCompanyName(companyName);
        setActorId(actorId);
        if(managedCompanyReferences != null)
            setManagedCompanyReferences(managedCompanyReferences);
        
        adopt(session);
    }
    
    public void adopt(Session session) {
        setSessionId(session.toSessionID());
        setCompanyId(session.getCompanyId());
        setUserCredentials(session.isUserCredentials());
    }
}
