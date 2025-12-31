/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.teamcharts.display;

import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class TeamChartsMBPrefDisplay extends AbstractTeamChartsDisplay {
    // The space between prefs: I   N   T   J
    private int prefSpacing;
    
    public TeamChartsMBPrefDisplay(int fontSize, int prefSpacing) {
        setSize(fontSize);
        setPrefSpacing(prefSpacing);
    }
    
    @Override
    public int getX() {
        return 179;
    }    
        
    @Override
    public int adjustX(int x) {
        // Because we write I N T J as four separate horizontal writes, we
        // add a space after each write: I[space]N[space]T[space]J
        x += prefSpacing;
        return x;
    }
}