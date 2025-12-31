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
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 *  * @author tom@parillume.com
 */
@Data
@JsonPropertyOrder({ "id", "entityType", "assessmentId", "type", "name", "description", "assessmentCategoryIds"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(name="CSResult", value=CSResult.class),        
    @JsonSubTypes.Type(name="EnnResult", value=EnnResult.class),
    @JsonSubTypes.Type(name="MBResult", value=MBResult.class)
})
public abstract class AssessmentResult extends Termible implements Entity {
    private String id;
    private String name;
    private String description;
    private String assessmentId;
    private List<String> assessmentCategoryIds = new ArrayList<>();
    
    @JsonIgnore
    public abstract AssessmentType getAssessmentType();
    
    // Type helps JSON de|serialization
    public String getType() {
        return getClass().getSimpleName();
    }
    public void setType(String type) {}
    
    public String getLabel() {
        return getName(); // Subclasses can override this
    }
    public void setLabel(String label) {
        // No-op: getLabel is hardcoded in methods rather than read from JSON
    }
    
    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.ASSESSMENT_RESULT;
    }
    
    public void addAssessmentCategoryId(String id) {
        getAssessmentCategoryIds().add(id);
    }
}
