/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.score;

import com.parillume.model.internal.MBPreferenceTheme;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public enum MBPreferenceScore { 
    
    INTROVERT("Introvert", MBPreferenceTheme.Focus, "Ponders; disengages to process; recharges alone"),
    EXTROVERT("Extrovert", MBPreferenceTheme.Focus, "Expresses; engages to process; recharges with others"),
    
    INTUITIVE("Intuitive", "N", MBPreferenceTheme.Input, "Abstract and interpretive; de/constructs concepts"),
    SENSORY("Sensory", MBPreferenceTheme.Input, "Literal, descriptive, and exploratory"),
    
    FEELING("Feeling", MBPreferenceTheme.Decisions, "Subjective; prioritizes internal state and experience"),
    THINKING("Thinking", MBPreferenceTheme.Decisions, "Objective, analytical; prioritizes external data"),
    
    PERCEIVING("Perceiving", MBPreferenceTheme.Structure, "Explores and adapts; focuses on the now"),
    JUDGING("Judging", MBPreferenceTheme.Structure, "Plans and predicts; focuses on the future");
    
    private String label;  
    private String symbol; // Set by default if not defined
    private MBPreferenceTheme theme;
    private String description;
    
    private MBPreferenceScore(String label, MBPreferenceTheme theme, String description) {
        this(label, label.substring(0,1), theme, description);
    }
    private MBPreferenceScore(String label, String symbol, MBPreferenceTheme theme, String description) {
        this.label = label;
        this.symbol = symbol;
        this.theme = theme;
        this.description = description;
    }
    
    public String getId() {
        return "MBPREF-" + name().toLowerCase();
    }
    
    public String getLabel() {
        return label;
    }
    public String getSymbol() {
        return symbol;
    }
    public MBPreferenceTheme getTheme() {
        return theme;
    }
    public String getDescription() {
        return description;
    }

    public static MBPreferenceScore getPreferenceByLetter(String prefStr) {
        for(MBPreferenceScore p: MBPreferenceScore.values()) {         
            if(prefStr.toUpperCase().equals(p.getSymbol()))
                return p;
        }
        return null;
    }
}
