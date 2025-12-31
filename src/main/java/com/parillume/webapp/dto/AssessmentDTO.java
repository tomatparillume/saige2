/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.parillume.model.internal.Assessment;
import com.parillume.model.internal.AssessmentCategory;
import com.parillume.model.internal.AssessmentCategoryType;
import com.parillume.model.internal.AssessmentResult;
import com.parillume.model.internal.AssessmentType;
import com.parillume.util.StringUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.Data;

/**
 * See AssessmentResultsDTO, which contains multiple AssessmentDTOs
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class AssessmentDTO {
    private static Comparator categoryComparator = new CategoryComparator();
    
    private String id;
    private String label;
    private String description;
    private String summary;
    
    private AssessmentType assessmentType;
    
    // e.g. Leadership Style
    private AssessmentCategoryType assessmentCategoryType; 

    // e.g. LeadershipStyle:Influencing, with associated assessment results
    private List<AssessmentCategoryDTO> categories = new ArrayList<AssessmentCategoryDTO>();
    
    public AssessmentDTO(Assessment assessment, AssessmentCategoryType categoryType) {
        setAssessmentType(assessment.getAssessmentType());
        setAssessmentCategoryType(categoryType);
        setLabel(assessment.getAssessmentType().getLabel());
        setId(assessment.getId());
        setDescription(assessment.getDescription());
        setSummary(assessment.getSummary());
    }
    
    public void add(AssessmentCategory category, AssessmentResult result) {
        Optional<AssessmentCategoryDTO> opt = categories.stream()
                                                          .filter( c -> StringUtil.nullEquals(c.getAssessmentCategory().getId(), category.getId()) )
                                                          .findFirst();
        AssessmentCategoryDTO categoryDTO = null;
        if(opt.isPresent()) {            
            categoryDTO = opt.get();
        } else {
            categoryDTO = new AssessmentCategoryDTO(category);
            categories.add(categoryDTO);
        }

        categoryDTO.getAssessmentResults().add(result);
        
        categories.sort(categoryComparator);
    }
    
    private static class CategoryComparator implements Comparator<AssessmentCategoryDTO> {
        @Override
        public int compare(AssessmentCategoryDTO catDTO1, AssessmentCategoryDTO catDTO2) {
            Integer order1 = catDTO1.getAssessmentCategory().getOrder();
            Integer order2 = catDTO2.getAssessmentCategory().getOrder();
            return order1 != null && order2 != null ?
                   order1.compareTo(order2) :
                   0;
        }
    }
}
