/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * A particular implementation of CultureCategory: e.g. Wanna Play?
 *  * @author tom@parillume.com
 */
@Data
public class CultureCategory extends AssessmentCategory { 
    @JsonIgnore
    @Override
    public AssessmentType getAssessmentType() {
        return AssessmentType.MyersBriggs;
    }
}
