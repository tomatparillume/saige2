/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.bargraph;

import lombok.Data;

/**
 * Creates individual bar graph images using SurveyMonkey formatting and colors.
 * These bar graphs are intended to be inserted in survey PowerPoints.
 * 
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class BarGraphData {
    
    private String barFileName; 
    private int[] barWidths; // multiple widths generates multiple bars
    private int barHeight;
    private int[] barPercentages = null;
    
    public BarGraphData(String barFileName, int[] barWidths, int barHeight, int[] barPercentages) {
        setBarFileName(barFileName);
        setBarWidths(barWidths);
        setBarHeight(barHeight);
        setBarPercentages(barPercentages);
    }
}
