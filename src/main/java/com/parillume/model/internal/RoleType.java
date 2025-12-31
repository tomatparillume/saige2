/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.parillume.util.model.EntityType;
import lombok.Data;

/**
 *
 *  * @author tom@parillume.com
 */
@Data
@JsonPropertyOrder({ "id", "entityType", "name", "description"})
public class RoleType implements Entity {
    private String id;
    private String name;
    private String description;
    
    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.ROLE_TYPE;
    }
}
