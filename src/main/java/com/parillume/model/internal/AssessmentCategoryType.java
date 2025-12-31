/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.parillume.util.model.EntityType;
import lombok.Data;

/**
 * e.g. Social Stance, Culture Category, Leadership Theme, Leadership Style
 *  * @author tom@parillume.com
 */
@Data
@JsonPropertyOrder({ "id", "entityType", "name", "description" })
public class AssessmentCategoryType implements Entity {
    private String id;
    private String name;
    private String description;
    
    private Integer order;
    
    // e.g. Leadership Theme has Leadership Style's as its parent id
    private String parentCategoryTypeId;
    
    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.ASSESSMENT_CATEGORY_TYPE;
    }
}
