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
import lombok.Data;

/**
 *
 *  * @author tom@parillume.com
 */
@Data
@JsonPropertyOrder({ "id", "entityType", "name", "description", "roleTypeId", "isActive"})
public class Role implements Entity {
    private String id;
    private String name;
    private String description;
    private boolean isActive = true;
    
    private String roleTypeId;
    
    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.ROLE;
    }
    
    public Role clone() {
        Role clone = new Role();
        clone.setActive(isActive());
        clone.setName(getName());
        clone.setDescription(getDescription());
        clone.setRoleTypeId(getRoleTypeId());
        clone.setId(StringUtil.createAlphanumericID());
        return clone;
    }    

    @Override
    public void validateFields(CompanyModel companyModel) throws Exception {        
        if(StringUtil.isEmpty(getId()))
            throw new Exception("Role is missing an id field");
    }    
}
