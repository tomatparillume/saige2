/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 *  * @author tom@parillume.com
 */
public class MBAssessment extends Assessment {
    
    @JsonIgnore
    @Override
    public AssessmentType getAssessmentType() {
        return AssessmentType.MyersBriggs;
    }
    
    @JsonIgnore
    @Override
    public int getOrder() { return 2; }
}
