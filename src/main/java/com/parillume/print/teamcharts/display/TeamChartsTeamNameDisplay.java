/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.teamcharts.display;

import com.parillume.print.teamcharts.TeamChartsWriter;
import lombok.Data;

/** 
 * Defines configuration for writing team names to team charts.
 * @author tmargolis
 * @author tom@parillume.com
 * See TeamChartsWriter
 */
@Data
public class TeamChartsTeamNameDisplay extends AbstractTeamChartsDisplay {
    private float yAdjustment;
    public TeamChartsTeamNameDisplay(int fontSize, float yAdjustment) {
        setSize(fontSize);
        setYAdjustment(yAdjustment);
    }
    
    @Override
    public int getX() {
        return 0; // TeamChartsWriter will center team name
    }
}
