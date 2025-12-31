/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 *
 *  * @author tom@parillume.com
 */
@Data
public class MBResult extends AssessmentResult {
    // e.g. "Mastermind"
    private String nickname;
    
    // e.g. Focus:I, Input:N, Decisions:T, Structure:J
    private Map<MBPreferenceTheme, String> themeToPreferenceId = new HashMap<>();
    
    public void addPreference(MBPreferenceTheme theme, String mbPreferenceId) {
        getThemeToPreferenceId().put(theme, mbPreferenceId);
    }
    
    @Override
    public String getLabel() {
        return getName() + ": " + getNickname();
    }
    
    @JsonIgnore
    @Override
    public AssessmentType getAssessmentType() {
        return AssessmentType.MyersBriggs;
    }
}
