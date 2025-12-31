/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.lexicon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.parillume.model.internal.Entity;
import com.parillume.util.model.EntityType;
import lombok.Data;

/**
 *
 *  * @author tom@parillume.com
 */
@Data
@JsonPropertyOrder({ "id", "entityType", "name", "description"})
public class Term implements Entity {
    private String id;
    private String name;
    private String description;
    
    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.TERM;
    }
}
