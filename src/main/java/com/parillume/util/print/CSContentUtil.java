/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util.content;

import com.parillume.model.score.CSStrengthScore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class CSContentUtil {
    
    public enum LeadershipTheme {
        STRATEGIC("Strategic", "STRATEGIC"),
        INFLUENCING("Influencing", "INFLUENCING"),
        EXECUTING("Executing", "EXECUTING"),
        RELATIONSHIPS("Relationships", "RELATIONSHIPS");
        
        private String dominantLabel;
        private String[] labels;
        
        private LeadershipTheme(String dominantLabel, String... labels) {
            this.dominantLabel = dominantLabel;
            this.labels = labels;
        }
        
        public String getDominantLabel() { return dominantLabel; }
        public String[] getLabels() { return labels; }
        public static int getLabelFontSize(boolean anatomySheet) { 
            return anatomySheet ? 6 : 8; 
        }   
        public static int getThemeGap(boolean anatomySheet) { 
            return anatomySheet ? 8 : 12; 
        }   
    }    
    
    /**
     * STRENGTH   RANK    DOMINANT
     *   X         5      X = 5+1
     *   Y         4      Y = 4+3 (Dominant)
     *   Y         3
     *   -         2      
     *   X         1
     * 
     *   X         5      X = 5
     *   Y         4      Y = 4+1 (Dominant): TWO Y's
     *   -         3
     *   -         2      
     *   Y         1
     * 
     *   X         5      X = 5
     *   Y         4      Y = 4+1 (Dominant): TWO Y's, and higher than Z
     *   Z         3      Z = 3+2
     *   Z         2      
     *   Y         1
     */
    public static LeadershipTheme getDominantTheme(List<CSStrengthScore> strengths) {
        Map<LeadershipTheme,Integer> themeToScoreTotal = new HashMap<>();
        Map<LeadershipTheme,Integer> themeToHighestScore = new HashMap<>();
        Map<LeadershipTheme,Integer> themeToCount = new HashMap<>();
        
        int currentScore = 5;
        for(CSStrengthScore strength: strengths) {
            LeadershipTheme theme = strength.getTheme();
            
            Integer scoreTotal = themeToScoreTotal.get(theme);
            themeToScoreTotal.put(theme, scoreTotal != null ?
                                         scoreTotal + currentScore : currentScore);
            
            Integer themeCount = themeToCount.get(theme);
            themeToCount.put(theme, themeCount != null ?
                                    ++themeCount : 1);
            
            if(!themeToHighestScore.containsKey(theme))
                themeToHighestScore.put(theme, currentScore);
                        
            currentScore--;
        }
        
        LeadershipTheme dominantTheme = null;
        int dominantThemeCount = 0;
        int dominantThemeHighestScore = 0;
        int globalHighestScore = 0;
        for(LeadershipTheme theme: themeToScoreTotal.keySet()) {
            int thisScore = themeToScoreTotal.get(theme);
            int thisHighestScore = themeToHighestScore.get(theme);
            int thisThemeCount = themeToCount.get(theme);
            
            if(dominantTheme == null) {
                dominantTheme = theme;
                dominantThemeCount = thisThemeCount;
                dominantThemeHighestScore = thisHighestScore;
                globalHighestScore = thisScore;
                continue;
            }
                // Most-frequent theme wins
            if( thisThemeCount > dominantThemeCount 
                ||
                // Tie for most-frequent: highest score wins
                (thisThemeCount == dominantThemeCount && thisScore > globalHighestScore) 
                || 
                // Tie for most-frequent and score: highest single score wins
                (thisThemeCount == dominantThemeCount && thisScore == globalHighestScore &&
                 thisHighestScore > dominantThemeHighestScore) 
              ) {
                dominantTheme = theme;
                dominantThemeCount = thisThemeCount;
                dominantThemeHighestScore = thisHighestScore;
                globalHighestScore = thisScore;                
            }
        }
        
        return dominantTheme;
    }
}
