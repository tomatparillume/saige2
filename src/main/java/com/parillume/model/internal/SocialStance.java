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
public class SocialStance extends AssessmentCategory {
    @JsonIgnore
    @Override
    public AssessmentType getAssessmentType() {
        return AssessmentType.Enneagram;
    }
}
