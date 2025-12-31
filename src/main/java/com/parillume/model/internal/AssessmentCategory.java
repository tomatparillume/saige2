/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.parillume.model.lexicon.Termible;
import com.parillume.util.model.EntityType;
import lombok.Data;

/**
 *  * @author tom@parillume.com
 */
@Data
@JsonPropertyOrder({ "id", "name", "description", "entityType", "assessmentCategoryTypeId", "termId"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(name="CultureCategory", value=CultureCategory.class),        
    @JsonSubTypes.Type(name="LeadershipStyle", value=LeadershipStyle.class),
    @JsonSubTypes.Type(name="LeadershipTheme", value=LeadershipTheme.class),
    @JsonSubTypes.Type(name="MBPreference", value=MBPreference.class),
    @JsonSubTypes.Type(name="SocialStance", value=SocialStance.class)
})
public abstract class AssessmentCategory extends Termible implements Entity {
    private String id;
    private String name;
    private String description;
    
    // e.g. 1:Strategic, 2:Influencing, 3:Executing, 4:Relationship-Building
    private Integer order;
    
    private String assessmentCategoryTypeId;
    private String termId;
        
    public abstract AssessmentType getAssessmentType();
    
    // Type helps JSON de|serialization
    public String getType() {
        return getClass().getSimpleName();
    }
    public void setType(String type) {}
            
    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.ASSESSMENT_CATEGORY;
    }
}
