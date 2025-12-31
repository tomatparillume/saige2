/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.onesheet.display;

import com.parillume.model.score.MBPreferenceScore;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public enum OneSheetMBPrefDisplay {
    // The coordinates define where the mask over the opposite preference will go:
    INTROVERT(MBPreferenceScore.INTROVERT, new float[]{145, 434, 107, 30}, // Masks EXTROVERT
                                           new float[]{371, 339.5f, 67, 20}), // Masks EXTROVERT
    EXTROVERT(MBPreferenceScore.EXTROVERT, new float[]{145, 464, 107, 30},    // Masks INTROVERT
                                           new float[]{372, 359.5f, 66, 20}), // Masks INTROVERT

    INTUITIVE(MBPreferenceScore.INTUITIVE, new float[]{154, 348, 107, 30},
                                           new float[]{378.5f, 291.5f, 64, 17}),
    SENSORY(MBPreferenceScore.SENSORY, new float[]{148, 378, 107, 30},
                                       new float[]{377, 308, 61.5f, 16}),

    FEELING(MBPreferenceScore.FEELING, new float[]{143, 265, 107, 30},
                                       new float[]{372, 241, 65, 19.5f}),
    THINKING(MBPreferenceScore.THINKING, new float[]{146, 295, 107, 30},
                                         new float[]{376, 260, 61.5f, 19}),

    PERCEIVING(MBPreferenceScore.PERCEIVING, new float[]{140, 178, 115, 25},
                                             new float[]{370.5f, 192, 67f, 14f}),
    JUDGING(MBPreferenceScore.JUDGING, new float[]{140, 201, 115, 43},
                                       new float[]{371.5f, 205, 67f, 27});
    
    private MBPreferenceScore preferenceScore;
    private float[] xywh;
    private float[] xywhAnatomy;
    
    private OneSheetMBPrefDisplay(MBPreferenceScore preferenceScore, 
                                  float[] xywh, float[] xywhAnatomy) {
        this.preferenceScore = preferenceScore;
        this.xywh = xywh;
        this.xywhAnatomy = xywhAnatomy;
    }
    
    public static float getColumnHeaderX(boolean anatomySheet) {
        return anatomySheet ? 407f : 197f;
    }

    public String getSymbol() { return preferenceScore.getSymbol(); }
    public float[] getXYWH(boolean anatomySheet) { 
        return anatomySheet ? xywhAnatomy : xywh; 
    }

    public static OneSheetMBPrefDisplay getDisplay(MBPreferenceScore preferenceScore)
    throws Exception {
        for(OneSheetMBPrefDisplay display: OneSheetMBPrefDisplay.values()) {
            if(preferenceScore == display.preferenceScore)
                return display;
        }
        throw new Exception("No Myers-Briggs preference display exists for MBPreferenceScore " + preferenceScore.name());
    }    
}