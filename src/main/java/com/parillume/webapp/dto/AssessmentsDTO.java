/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.parillume.model.CorpusModel;
import com.parillume.model.internal.Assessment;
import com.parillume.model.internal.AssessmentCategory;
import com.parillume.model.internal.AssessmentCategoryType;
import com.parillume.model.internal.AssessmentResult;
import com.parillume.model.internal.AssessmentType;
import com.parillume.util.StringUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * <pre>
    {
        "assessmentSubcategoryTypes": [ 
            {
                "id": "LT-yesand",
                "name": "Yes, And",
                "description": "blah blah"
            },
            {
                "id": "LT-tellmemore",
                "name": "Tell Me More",
                "description": "blah blah"
            },
        ],
       "assessments" [
        {
            "assessmentType": "CliftonStrengths",
            "label": "CliftonStrengths®",
            "description": "Some description",
            "assessmentCategoryType": {
                "name": "Leadership Style",
                "description": "Leadership Style description goes here",
                "id": "ACT-leadershipstyle"
            },
            "id": null,
            "categories": [
                {
                    "assessmentCategory": {
                        "name": "Executing",
                        "termId": null,
                        "assessmentType": "CliftonStrengths",
                        "termIdToValue": {
                            Constants.SUPERPOWER_TERMID: "Executing Style superpowers go here",
                            Constants.KRYPTONITE_TERMID: "Executing Style kryptonite goes here"
                        },
                        "assessmentCategoryTypeId": "ACT-leadershipstyle",
                        "description": "Lean on Executors to drive a project forward and get it across the finish line. Give your executing team members the concrete info they need to get ?er done, and then get out of their way.",
                        "id": "LS-executing",
                        "type": "LeadershipStyle"
                    },
                    "assessmentResults": [
                        {
                            "termIdToValue": {
                                Constants.SUPERPOWER_TERMID: "Achiever Strength superpowers go here",
                                Constants.KRYPTONITE_TERMID: "Achiever Strength kryptonite goes here"
                            },
                            "name": "Achiever",
                            "assessmentCategoryIds": [
                                "LS-executing",
                                "LT-complete"
                            ],
                            "description": "Achiever strength description goes here",
                            "id": "CS-achiever",
                            "type": "CSResult",
                            "assessmentId": "CS-assessment"
                        },
                        ...
                    ]
                },
                {
                    "assessmentCategory": {
                        "name": "Influencing",
                        ...
                    },
                    "assessmentResults": [
                        ...
                    ]
                }
            ] // END ""categories" for CliftonStrengths 
        }, // END CliftonStrengths assessment
        {
            "assessmentType": "CliftonStrengths",
            ...
        }
    ] } // END "assessments"    
</pre>
*
*  * @author tom@parillume.com
*/
@Data
public class AssessmentsDTO {
           
    private List<AssessmentDTO> assessments = new ArrayList<>();
    
    private List<AssessmentCategory> assessmentSubcategories = new ArrayList<>();
    
    public AssessmentsDTO(CorpusModel corpus) { 
        
        // LeadershipStyle, CultureCategory...
        List<AssessmentCategoryType> assessmentCategoryTypes = corpus.getAssessmentCategoryTypes();
        
        List<String> subTypeIds = assessmentCategoryTypes.stream()
                                                         .filter(c -> !StringUtil.isEmpty(c.getParentCategoryTypeId()))
                                                         .map(c -> c.getId())
                                                         .collect(Collectors.toList());
        
        // LeadershipStyle:Influencing, LeadershipStyle:Executing, CultureCategory:Wanna Play? ...
        List<AssessmentCategory> assessmentCategories = corpus.getAssessmentCategories();
        
        assessmentSubcategories = assessmentCategories.stream()
                                                      .filter(c -> subTypeIds.contains(c.getAssessmentCategoryTypeId()))
                                                      .collect(Collectors.toList());
        List<String> subcategoryIds = assessmentSubcategories.stream()
                                                             .map(s -> s.getId())
                                                             .collect(Collectors.toList());
        
        List<Assessment> corpusAssessment = corpus.getAssessments();
        
        // type: Enneagram, CliftonStrengths...
        for(AssessmentType assessmentType: AssessmentType.values()) {
            List<ComparableAssessmentResult> resultsList = corpus.getAssessmentResults()
                                                                 .stream()
                                                                 .filter(r -> assessmentType == r.getAssessmentType())
                                                                 .map(r -> new ComparableAssessmentResult(r, 
                                                                                                          assessmentCategories,
                                                                                                          subcategoryIds))
                                                                 .sorted()
                                                                 .collect(Collectors.toList());
            
            for(ComparableAssessmentResult categorizedResult: resultsList) {
                AssessmentResult result = categorizedResult.getResult();
                
                // "LT-wannaplayid", "LT-yesand" ...
                List<String> resultCategoryIds = result.getAssessmentCategoryIds();
                
                // e.g. LeadershipTheme:Influencing, LeadershipType:Yes And
                List<AssessmentCategory> categories = assessmentCategories.stream()
                                                                          .filter(c -> resultCategoryIds.contains(c.getId()))
                                                                          .collect(Collectors.toList());
                if(categories.isEmpty())
                    continue;
                                
                // For this result:
                // LeadershipStyle:Influencing, LeadershipStyle:Executing, CultureCategory:Wanna Play? ...
                AssessmentCategory category = categories.get(0);
                            
                // "ACT-leadershipstyle", "ACT-culturecategory" ...
                String categoryTypeId = category.getAssessmentCategoryTypeId();
                Optional<AssessmentCategoryType> catTypeOpt = assessmentCategoryTypes.stream()
                                                                                     .filter(t -> StringUtil.nullEquals(t.getId(), categoryTypeId))
                                                                                     .findFirst();
                if(catTypeOpt.isEmpty())
                    continue;
                
                // LeadershipStyle, CultureCategory
                AssessmentCategoryType categoryType = catTypeOpt.get();

                Optional<Assessment> corpusAssessmentOpt = corpusAssessment.stream()
                                                                           .filter(a -> StringUtil.nullEquals(a.getId(), result.getAssessmentId()))
                                                                           .findFirst();
                if(corpusAssessmentOpt.isEmpty())
                    continue; //TJMTJM log
                
                Assessment assessment = corpusAssessmentOpt.get();
                
                Optional<AssessmentDTO> opt = assessments.stream()  
                                                         .filter( a -> StringUtil.nullEquals(a.getAssessmentCategoryType().getId(), categoryType.getId()) )
                                                         .findFirst();
                AssessmentDTO assessmentDTO = null;
                if(opt.isPresent()) {
                    assessmentDTO = opt.get();
                } else {
                    assessmentDTO = new AssessmentDTO(assessment, categoryType);
                    assessments.add(assessmentDTO);
                }                
                assessmentDTO.add(category, result);
            }
        }
    }
    
    // Allows us to sort by assessment category and subcategory
    private class ComparableAssessmentResult implements Comparable<ComparableAssessmentResult> {
        private AssessmentResult result;
        private Integer mainCategoryOrder = 0;
        // Subcategory: e.g. LeadershipTheme is a child of LeadershipStyle
        private Integer subCategoryOrder = 0;
        
        public ComparableAssessmentResult(AssessmentResult result, 
                                           List<AssessmentCategory> assessmentCategories,
                                           List<String> assessmentSubcategoryIds) {
            this.result = result;
            
            Map<String, AssessmentCategory> idToCategory = assessmentCategories.stream()
                                                                               .collect(Collectors.toMap(c->c.getId(), c->c));            
            for(String categoryId: result.getAssessmentCategoryIds()) {
                AssessmentCategory category = idToCategory.get(categoryId);

                Integer order = category.getOrder();
                if(order == null)
                    order = 0;
                
                if(assessmentSubcategoryIds.contains(categoryId))
                    subCategoryOrder = order;
                else
                    mainCategoryOrder = order;
            }
        }
        
        public AssessmentResult getResult() { return result; }
        public String getResultName() { return result.getName(); }
        public Integer getMainCategoryOrder() { return mainCategoryOrder; }
        public Integer getSubCategoryOrder() { return subCategoryOrder; }
        
        @Override
        public int compareTo(ComparableAssessmentResult o) {
            return Comparator.comparing(ComparableAssessmentResult::getMainCategoryOrder)
                             .thenComparing(ComparableAssessmentResult::getSubCategoryOrder)                      
                             .thenComparing(ComparableAssessmentResult::getResultName)
                             .compare(this, o);
        }
    }
}