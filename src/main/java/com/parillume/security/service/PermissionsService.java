/*
 * Copyright(c) 2023, Second Phase LLC., All rights reserved worldwide
 */
package com.parillume.security.service;

import com.parillume.db.model.DBPermissibleActions;
import com.parillume.error.InvalidPermissionsException;
import com.parillume.security.PermissibleAction;
import com.parillume.security.Session;
import com.parillume.security.repository.PermissibleActionsRepository;
import com.parillume.util.Constants;
import com.parillume.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Service
public class PermissionsService {
    @Autowired
    private PermissibleActionsRepository repository;
    
    public List<PermissibleAction> getPermissibleActions(String username, String companyId) {        
        Optional<DBPermissibleActions> opt = repository.findByUsernameAndCompanyId(username, companyId);
        
        if(opt.isEmpty() && StringUtil.nullEquals(Constants.PLUME_COMPANY_ID, companyId))
            opt = repository.findByCompanyId(companyId);
        
        return (opt != null && opt.isPresent()) ?
                opt.get().getActions() :
                new ArrayList();
    }
    
    public void verifyPermissions(Session session, String endpoint) 
    throws Exception {        
        if( !isPermitted(session.getUsername(), session.getCompanyId(), endpoint) ) {
            throw new InvalidPermissionsException(session.getUsername() + " does not have permissions to execute " + endpoint);
        }
    }    
    
    @PostConstruct
    public void seedParillumeActions() {
        Optional<DBPermissibleActions> opt = repository.findByCompanyId(Constants.PLUME_COMPANY_ID);

        if(opt.isEmpty()) {
            DBPermissibleActions parillumeActions = new DBPermissibleActions(null, Constants.PLUME_COMPANY_ID, Arrays.asList(PermissibleAction.ALL));    
            repository.save(parillumeActions);
            return;
        } 

        DBPermissibleActions dbActions = opt.get();
        if(!dbActions.getActions().contains(PermissibleAction.ALL) ) {
            dbActions.getActions().add(PermissibleAction.ALL);    
            repository.save(dbActions);
        }
    }
    
    private boolean isPermitted(Session session, String endpoint) {
        return isPermitted(session.getUsername(), session.getCompanyId(), endpoint);
    }
    private boolean isPermitted(String username, String companyId, String endpoint) {
        Collection<PermissibleAction> thisUserActions = getPermissibleActions(username,companyId);
        if(thisUserActions == null)
            return false;
        
        if(thisUserActions.contains(PermissibleAction.ALL))
            return true;
    
        boolean unrestrictedEndpoint = true;
        
        for(PermissibleAction action: PermissibleAction.values()) {
            if( action.getEndpoints().contains(endpoint) ) {
                unrestrictedEndpoint = false;            
                if(thisUserActions.contains(action)) {
                    return true; // Explicit permission to execute the endpoint
                }
            }
        }
        
        // If endpoint is not listed, it is permitted:
        return unrestrictedEndpoint;
    }    
}
