/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.onesheet.display;

import com.parillume.model.score.CSStrengthScore;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public enum OneSheetStrengthDisplay {    
    // This order matches the order in the CliftonStrengths team chart:
    ANALYTICAL(CSStrengthScore.ANALYTICAL, "®"),
    CONTEXT(CSStrengthScore.CONTEXT, "®"),
    FUTURISTIC(CSStrengthScore.FUTURISTIC, "®"),
    IDEATION(CSStrengthScore.IDEATION, "®"),
    INPUT(CSStrengthScore.INPUT, "®"),
    INTELLECTION(CSStrengthScore.INTELLECTION, "®"),
    LEARNER(CSStrengthScore.LEARNER, "®"),
    STRATEGIC(CSStrengthScore.STRATEGIC, "®"),

    ACTIVATOR(CSStrengthScore.ACTIVATOR, "®"),
    COMMAND(CSStrengthScore.COMMAND, "®"),
    COMMUNICATION(CSStrengthScore.COMMUNICATION, "®"),
    COMPETITION(CSStrengthScore.COMPETITION, "®"),
    MAXIMIZER(CSStrengthScore.MAXIMIZER, "®"),
    SELF_ASSURANCE(CSStrengthScore.SELF_ASSURANCE, "®"),
    SIGNIFICANCE(CSStrengthScore.SIGNIFICANCE, "®"),
    WOO(CSStrengthScore.WOO, "®"),

    ACHIEVER(CSStrengthScore.ACHIEVER, "®"),
    ARRANGER(CSStrengthScore.ARRANGER, "®"),
    BELIEF(CSStrengthScore.BELIEF, "®"),
    CONSISTENCY(CSStrengthScore.CONSISTENCY, "®"),
    DELIBERATIVE(CSStrengthScore.DELIBERATIVE, "®"),
    DISCIPLINE(CSStrengthScore.DISCIPLINE, "®"),
    FOCUS(CSStrengthScore.FOCUS, "®"),
    RESPONSIBILITY(CSStrengthScore.RESPONSIBILITY, "®"),
    RESTORATIVE(CSStrengthScore.RESTORATIVE, "TM"),

    ADAPTABILITY(CSStrengthScore.ADAPTABILITY, "®"),
    CONNECTEDNESS(CSStrengthScore.CONNECTEDNESS, "®"),
    DEVELOPER(CSStrengthScore.DEVELOPER, "®"),
    EMPATHY(CSStrengthScore.EMPATHY, "®"),
    HARMONY(CSStrengthScore.HARMONY, "®"),
    INCLUDER(CSStrengthScore.INCLUDER, "®"),
    INDIVIDUALIZATION(CSStrengthScore.INDIVIDUALIZATION, "®"),
    POSITIVITY(CSStrengthScore.POSITIVITY, "®"),
    RELATOR(CSStrengthScore.RELATOR, "®");
        
        
    public static int getColumnHeaderFontSize(boolean anatomySheet) { 
        return anatomySheet ? 8 : 13; 
    }
    public static float getColumnHeaderY(boolean anatomySheet) {
        return anatomySheet ? 394.8f : 525f;
    }
    
    public static float getLabelFontSize(boolean anatomySheet) {
        return anatomySheet ? 8.4f : 13f;
    }
    public static float getLabelX(boolean anatomySheet) {
        return anatomySheet ? 334f : 77f;
    }
    public static float getLabelY(boolean anatomySheet) {
        return anatomySheet ? 367f : 475f;
    }
    public static int getLabelVerticalBuffer(boolean anatomySheet) {
        return anatomySheet ? 39 : 65;
    }
    
    public static float getDescriptionFontSize() {
        return 9.5f;
    }
    public static float getDescriptionX() {
        return 281f;
    }
    public static float getDescriptionY() {
        return 580.5f;
    }
    public static int getDescriptionVerticalBuffer() {
        return 28;
    }
    
    private CSStrengthScore strengthScore;

    private String trademark;

    private OneSheetStrengthDisplay(CSStrengthScore strengthScore, String trademark) {
        this.strengthScore = strengthScore;
        this.trademark = trademark;
    }
    
    public static OneSheetStrengthDisplay getDisplay(CSStrengthScore strengthScore) 
    throws Exception {
        for(OneSheetStrengthDisplay display: OneSheetStrengthDisplay.values()) {
            if(strengthScore == display.getStrengthScore())
                return display;
        }
        throw new Exception("No Clifton Strengths display exists for CSStrengthScore " + strengthScore.name());
    }

    public CSStrengthScore getStrengthScore() { return strengthScore; }
    public String getLabel() { return getStrengthScore().getLabel(); }
    public String getTrademark() { return trademark; }        
}
