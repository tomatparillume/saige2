/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.teamcharts.display;

import com.parillume.print.teamcharts.TeamChartsWriter;
import lombok.Data;

/** 
 * Defines configuration for writing animals to the Myers-Briggs rows of a team chart.
 * @author tmargolis
 * @author tom@parillume.com
 * See TeamChartsWriter
 */
@Data
public class TeamChartsMBAnimalsDisplay extends AbstractTeamChartsDisplay {     
    
    public TeamChartsMBAnimalsDisplay(int fontSize) {
        setSize(fontSize);
    }
    
    @Override
    public int getX() {
        return 587; // The center of the ANIMAL display box
    }
}
