/*
 * Copyright(c) 2023, Second Phase LLC., All rights reserved worldwide
 */
package com.parillume.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines actions a logged-in user can perform; enforced in Controller endpoints
 * @author tmargolis
 * @author tom@parillume.com
 */
public enum PermissibleAction {
    ALL(),
    
    // LogsController
    MANAGE_LOGS("getlogs"),
    
                   // CompanyController
    MANAGE_COMPANY("createcompany", "updatecompany", "deletecompany", "changecompanycredentials",
                   // UserController
                   "importusers", "upsertusers", "deleteusers",
                   // ImageController
                   "upsertimage", "deleteimage"),
    
    // CorpusController
    MANAGE_CORPUS("addcorpus", "updatecorpus", "deletecorpus"),
    
    // UserController
    MANAGE_USER("upsertusers", "deleteusers", "updateself"),
    
    // DisplayController
    VIEW_COMPANY("getcompanydto");

    private List<String> endpoints = new ArrayList<>();
    
    private PermissibleAction() {}
    
    private PermissibleAction(String... endpoints) {
        this.endpoints = Arrays.asList(endpoints);
    }
    
    @JsonIgnore
    public List<String> getEndpoints() {
        return endpoints;
    }
    
    public static PermissibleAction getAction(String endpoint) {
        if(endpoint.startsWith("/"))
            endpoint = endpoint.substring(1);
        
        for(PermissibleAction action: PermissibleAction.values()) {
            if(action.endpoints.contains(endpoint))
                return action;
        }
        
        return null;
    }
    
    public static List<PermissibleAction> getIndividualActions() {
        return Arrays.asList(PermissibleAction.values())
                                              .stream()
                                              .filter(p -> ALL != p)
                                              .collect(Collectors.toList());
    }
}
