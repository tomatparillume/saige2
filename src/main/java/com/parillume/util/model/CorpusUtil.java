/*
 * Copyright(c) 2024, Billtrust Inc., All rights reserved worldwide
 */
package com.parillume.util.model;

import com.parillume.model.CorpusModel;
import com.parillume.model.external.User;
import com.parillume.model.internal.AssessmentResult;
import com.parillume.model.internal.AssessmentType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class CorpusUtil {
    public static Map<String,AssessmentResult> getAssessmentResultsById(CorpusModel corpus, AssessmentType assessmentType) {
        List<AssessmentResult> resultCorpus = corpus.getAssessmentResults();
        return resultCorpus.stream()
                           .filter(r -> assessmentType == r.getAssessmentType())
                           .collect(Collectors.toMap(r->r.getId(), r->r));       
    }
    
    public static List<String> getResultIdsForUser(Map<String,AssessmentResult> idToResult, User user) {
        return user.getAssessmentResultIds().stream()
                                            .filter(r -> idToResult.keySet().contains(r))
                                            .collect(Collectors.toList()); 
    }
}
