/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model;

import com.parillume.model.internal.Assessment;
import com.parillume.model.internal.AssessmentCategory;
import com.parillume.model.internal.AssessmentCategoryType;
import com.parillume.model.internal.AssessmentResult;
import com.parillume.model.internal.RoleType;
import com.parillume.model.lexicon.Term;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class CorpusModel {
    public static final String CURRENT_VERSION = "1.0";
    
    private static final Comparator categoryComparator = new CategoryTypeComparator();
    
    private String version = CURRENT_VERSION;
    private List<Term> terms = new ArrayList<>();
    private List<AssessmentCategoryType> assessmentCategoryTypes = new ArrayList<>();
    private List<AssessmentCategory> assessmentCategories = new ArrayList<>();
    private List<Assessment> assessments = new ArrayList<>();
    private List<AssessmentResult> assessmentResults = new ArrayList<>();
    private List<RoleType> roleTypes = new ArrayList<>();
    
    public List<AssessmentCategoryType> getAssessmentCategoryTypes() {
        assessmentCategoryTypes.sort(categoryComparator);
        return assessmentCategoryTypes;
    }
    
    private static class CategoryTypeComparator implements Comparator<AssessmentCategoryType> {
        @Override
        public int compare(AssessmentCategoryType type1, AssessmentCategoryType type2) {
            Integer order1 = type1.getOrder();
            Integer order2 = type2.getOrder();
            return order1 != null && order2 != null ?
                   order1.compareTo(order2) :
                   0;
        }
    }
}
