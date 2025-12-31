/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 *  * @author tom@parillume.com
 */
@Data
public class MBPreference extends AssessmentCategory {
    private String nickName;
    
    @JsonIgnore
    @Override
    public AssessmentType getAssessmentType() {
        return AssessmentType.MyersBriggs;
    }
}
