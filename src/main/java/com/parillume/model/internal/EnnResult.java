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
public class EnnResult extends AssessmentResult {    
    // e.g. "Challenger"
    private String nickname;
    
    private LeadershipStyle leadershipStyle;    
    
    @Override
    public String getLabel() {
        return getName() + ": " + getNickname();
    }
    
    @JsonIgnore
    @Override
    public AssessmentType getAssessmentType() {
        return AssessmentType.Enneagram;
    }
}
