/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.parillume.model.CorpusModel;
import com.parillume.model.internal.Assessment;
import com.parillume.model.internal.AssessmentResult;
import com.parillume.model.internal.AssessmentType;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;

/**
*  * @author tom@parillume.com
*/
@Data
public class AssessmentResultsDTO {
    
    private static Comparator assessmentOrderComparator = new AssessmentOrderComparator();
    private static Comparator resultsLabelComparator = new ResultsLabelComparator();
           
    private Map<String, List<AssessmentResult>> typeLabelToResults = new LinkedHashMap<>();
    
    public AssessmentResultsDTO(CorpusModel corpus) { 
        List<Assessment> corpusAssessments = corpus.getAssessments();
        Collections.sort(corpusAssessments, assessmentOrderComparator);
        
        // type: Enneagram, CliftonStrengths...
        for(Assessment assessment: corpusAssessments) {
            AssessmentType assessmentType = assessment.getAssessmentType();
            
            List<AssessmentResult> results = corpus.getAssessmentResults()
                                                                 .stream()
                                                                 .filter(r -> assessmentType == r.getAssessmentType())
                                                                 .collect(Collectors.toList());
            Collections.sort(results, resultsLabelComparator);
            typeLabelToResults.put(assessmentType.getLabel(), results);
        }
    }
    
    private static class AssessmentOrderComparator implements Comparator<Assessment> {
        @Override
        public int compare(Assessment r1, Assessment r2) {
            return ((Integer)r1.getOrder()).compareTo(r2.getOrder());
        }
    }
    
    private static class ResultsLabelComparator implements Comparator<AssessmentResult> {
        @Override
        public int compare(AssessmentResult r1, AssessmentResult r2) {
            return r1.getLabel().compareTo(r2.getLabel());
        }
    }
}