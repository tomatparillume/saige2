/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;


/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(name="TableDTO", value=TableDTO.class),
    @JsonSubTypes.Type(name="CompanyDTO", value=CompanyDTO.class)
})
public abstract class AbstractWebAppDTO {
    private String id;
    private String label;
    
    // Type helps JSON de|serialization    
    public String getType() {
        return getClass().getSimpleName();
    }
    public void setType(String type) {}
}
