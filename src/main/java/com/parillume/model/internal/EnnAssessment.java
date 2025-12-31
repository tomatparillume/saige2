/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 *  * @author tom@parillume.com
 */
public class EnnAssessment extends Assessment {
    
    @JsonIgnore
    @Override
    public AssessmentType getAssessmentType() {
        return AssessmentType.Enneagram;
    }
    
    @JsonIgnore
    @Override
    public int getOrder() { return 3; }
}
