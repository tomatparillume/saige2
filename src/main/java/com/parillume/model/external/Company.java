/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.parillume.model.CompanyModel;
import com.parillume.model.internal.Entity;
import com.parillume.util.StringUtil;
import com.parillume.util.model.EntityType;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 *  * @author tom@parillume.com
 */
@Data
@JsonPropertyOrder({ "id", "entityType", "name", "isActive"})
public class Company implements Entity {
    private String id;
    private String name;
    private boolean isActive = true;
    private int chatsPerMonth = 1000;
    
    private String googleFolderURL;
    
    private String username = "";
    private String password;
    
    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.COMPANY;
    }
    
    public Company clone() {
        Company clone = new Company();
        clone.setActive(isActive());
        clone.setName(getName());
        clone.setChatsPerMonth(getChatsPerMonth());
        clone.setGoogleFolderURL(getGoogleFolderURL());
        clone.setId(StringUtil.createAlphanumericID());
// NO:        
//        clone.setUsername(getUsername());
//        clone.setPassword(getPassword());
        return clone;
    }    
    
    @Override
    public void validateFields(CompanyModel companyModel) throws Exception {
        List<String> missingFields = new ArrayList<>();
        
        if(StringUtil.isEmpty(getId()))
            missingFields.add("id");
        if(StringUtil.isEmpty(getName()))
            missingFields.add("name");
        if(StringUtil.isEmpty(getUsername()))
            missingFields.add("username");
        if(StringUtil.isEmpty(getPassword()))
            missingFields.add("password");
        
        if(!missingFields.isEmpty())
            throw new Exception("Company is missing required fields: " + missingFields);
    }
}
