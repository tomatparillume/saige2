/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util.content;

import com.parillume.model.score.CSStrengthScore;
import com.parillume.util.content.CSContentUtil.LeadershipTheme;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class CSContentUtilTest {
    @Test
    public void getDominantTheme_test() throws Exception {
        List<CSStrengthScore> strengths = new ArrayList<>();
        
        // Most-frequent wins
        strengths.add(CSStrengthScore.DEVELOPER); // Relationships
        strengths.add(CSStrengthScore.ACTIVATOR); // Influencing
        strengths.add(CSStrengthScore.INPUT); // Strategic
        strengths.add(CSStrengthScore.ACHIEVER); // Executing
        strengths.add(CSStrengthScore.BELIEF); // Executing        
        assertEquals(LeadershipTheme.EXECUTING, CSContentUtil.getDominantTheme(strengths));
        
        // Tie for most-frequent: highest score wins
        strengths.clear();
        strengths.add(CSStrengthScore.DEVELOPER); // Relationships
        strengths.add(CSStrengthScore.BELIEF); // Executing   
        strengths.add(CSStrengthScore.ACHIEVER); // Executing
        strengths.add(CSStrengthScore.INPUT); // Strategic     
        strengths.add(CSStrengthScore.EMPATHY); // Relationships
        assertEquals(LeadershipTheme.EXECUTING, CSContentUtil.getDominantTheme(strengths));
        
        // Tie for most-frequent and score: highest single score wins
        strengths.clear();
        strengths.add(CSStrengthScore.DEVELOPER); // Relationships
        strengths.add(CSStrengthScore.BELIEF); // Executing   
        strengths.add(CSStrengthScore.INPUT); // Strategic   
        strengths.add(CSStrengthScore.ACHIEVER); // Executing  
        strengths.add(CSStrengthScore.EMPATHY); // Relationships    
        assertEquals(LeadershipTheme.RELATIONSHIPS, CSContentUtil.getDominantTheme(strengths));
    }
}
