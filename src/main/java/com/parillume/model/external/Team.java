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
import java.util.Arrays;
import java.util.List;
import lombok.Data;

/**
 *
 *  * @author tom@parillume.com
 */
@Data
@JsonPropertyOrder({ "id", "entityType", "name", "description", "isActive", "companyIds"})
public class Team implements Entity {
    private String id;
    private String name;
    private String description;
    private boolean isActive = true;
    
    private List<String> companyIds = new ArrayList<>();
    
    public Team() {}
    
    public Team(String name, String... companyIds) {
        setName(name);
        setCompanyIds( new ArrayList(Arrays.asList(companyIds)) );
        setId(StringUtil.createAlphanumericID());
    }
    
    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.TEAM;
    }
    
    public Team clone() {
        Team clone = new Team(getName());
        clone.setActive(isActive());
        clone.setDescription(getDescription());
        return clone;
    }    
    
    @Override
    public void validateFields(CompanyModel companyModel) throws Exception {
        List<String> missingFields = new ArrayList<>();
        
        if(StringUtil.isEmpty(getId()))
            missingFields.add("id");
        if(StringUtil.isEmpty(getName()))
            missingFields.add("name");
        if(getCompanyIds().isEmpty())
            missingFields.add("company id(s)");
        
        if(!missingFields.isEmpty())
            throw new Exception("Team is missing required fields: " + missingFields);
    }    
    
    public void addCompanyId(String id) {
        getCompanyIds().add(id);
    }
}
