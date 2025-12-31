/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.teamcharts.display;

import com.parillume.print.teamcharts.TeamChartsWriter;
import lombok.Data;

/** 
 * Defines configuration for writing 5 strength circles to the CliftonStrengths 
 * chart of a team chart.
 * @author tmargolis
 * @author tom@parillume.com
 * See TeamChartsWriter
 */
@Data
public class TeamChartsStrengthCircleDisplay extends AbstractTeamChartsDisplay {     
    
    private int x;
    
    // Horizontal spacing of strength columns
    private float circleSpacing;
    
    public TeamChartsStrengthCircleDisplay(int x, float circleSpacing) {
        this.x = x;
        this.circleSpacing = circleSpacing;
    }
    
    @Override
    public int getX() {
        return x;
    }
    
    /**
     * @param strengthIndex: The index location on the CliftonStrengths PDF chart of each circle;
     *                       e.g. Analytical is index 0; Maximizer is index 12
     */
    public float getX(int strengthIndex) {
        return getX() + (circleSpacing * strengthIndex);
    } 
}
