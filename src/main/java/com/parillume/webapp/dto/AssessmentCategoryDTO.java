/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.parillume.model.internal.AssessmentCategory;
import com.parillume.model.internal.AssessmentResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;

/**
 *               
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class AssessmentCategoryDTO {
    private AssessmentCategory assessmentCategory;
    
    private List<AssessmentResult> assessmentResults = new ArrayList<>();

    public AssessmentCategoryDTO(AssessmentCategory assessmentCategory) {
        setAssessmentCategory(assessmentCategory);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AssessmentCategoryDTO other = (AssessmentCategoryDTO) obj;
        return !Objects.equals(this.assessmentCategory.getId(), other.assessmentCategory.getId());
    }
    
}
