/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.parillume.util.model.EntityType;
import lombok.Data;

/**
 *
 *  * @author tom@parillume.com
 */
@Data
@JsonPropertyOrder({ "id", "type", "entityType", "trademarkRightsSymbol", "disclaimer", "description" })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(name="CSAssessment", value=CSAssessment.class),        
    @JsonSubTypes.Type(name="EnnAssessment", value=EnnAssessment.class),
    @JsonSubTypes.Type(name="MBAssessment", value=MBAssessment.class)
})
public abstract class Assessment implements Entity {
    private String id;
    private String description;
    private String disclaimer;
    private String summary;
    
    @JsonIgnore
    public abstract AssessmentType getAssessmentType();
    
    @JsonIgnore
    public abstract int getOrder();
    
    @JsonIgnore
    @Override
    public EntityType getEntityType() {
        return EntityType.ASSESSMENT;
    }
    
    @JsonIgnore
    public String getTrademarkRightsSymbol() {
        return getAssessmentType().getTrademarkSymbol();
    }
    
    @JsonIgnore
    public String getLabel() {
        return getAssessmentType().getLabel();
    }
    
    // Type helps JSON de|serialization
    public String getType() {
        return getClass().getSimpleName();
    }
    public void setType(String type) {}
}
