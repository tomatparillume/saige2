/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 *  * @author tom@parillume.com
 */
public class CSAssessment extends Assessment {
    
    @JsonIgnore
    @Override
    public AssessmentType getAssessmentType() {
        return AssessmentType.CliftonStrengths;
    }
    
    @Override
    public String getTrademarkRightsSymbol() {
        return "®";
    }
    
    @JsonIgnore
    @Override
    public int getOrder() { return 1; }
}
