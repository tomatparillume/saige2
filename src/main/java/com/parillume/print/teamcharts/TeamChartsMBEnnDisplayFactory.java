/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.teamcharts;

import com.parillume.print.teamcharts.display.AbstractTeamChartsDisplay;
import com.parillume.print.teamcharts.display.TeamChartsEnnScoreDisplay;
import com.parillume.print.teamcharts.display.TeamChartsMBAnimalsDisplay;
import com.parillume.print.teamcharts.display.TeamChartsNameDisplay;
import com.parillume.print.teamcharts.display.TeamChartsMBPrefDisplay;
import com.parillume.print.teamcharts.display.TeamChartsTeamNameDisplay;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class TeamChartsMBEnnDisplayFactory {
    
    private static Map<List<Integer>, List<AbstractTeamChartsDisplay>> 
                    teamSizesToDisplays = new HashMap<>();  
    static {     
        putMBEnneagramDisplays(432, 35f, // y, ySpacing
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(13, 200),  // fontSize, nameWrapWidth
                new TeamChartsMBPrefDisplay(17, 101), // fontSize, prefSpacing
                new TeamChartsMBAnimalsDisplay(11),  // fontSize
                new TeamChartsEnnScoreDisplay(12),    // label fontSize, 
                new TeamChartsTeamNameDisplay(16, 95) // fontSize, yAdjustment
            },
            2, 3, 4);
        putMBEnneagramDisplays(460.5f, 28.5f, 
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(13, 150),   
                new TeamChartsMBPrefDisplay(17, 101), 
                new TeamChartsMBAnimalsDisplay(11),  
                new TeamChartsEnnScoreDisplay(11), 
                new TeamChartsTeamNameDisplay(16, 95)
            },
            5);
        putMBEnneagramDisplays(462.5f, 24.2f, // y, ySpacing
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(13, 150),   
                new TeamChartsMBPrefDisplay(17, 101),
                new TeamChartsMBAnimalsDisplay(11),   
                new TeamChartsEnnScoreDisplay(10),
                new TeamChartsTeamNameDisplay(16, 95)   
            },
            6);
        putMBEnneagramDisplays(435, 35.6f, 
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(13, 150),
                new TeamChartsMBPrefDisplay(17, 101), 
                new TeamChartsMBAnimalsDisplay(11),
                new TeamChartsEnnScoreDisplay(12), 
                new TeamChartsTeamNameDisplay(16, 95)
            },
            7, 8, 9, 10);
        putMBEnneagramDisplays(455, 28.6f, 
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(13, 150),
                new TeamChartsMBPrefDisplay(17, 101), 
                new TeamChartsMBAnimalsDisplay(11),
                new TeamChartsEnnScoreDisplay(11), 
                new TeamChartsTeamNameDisplay(16, 95) // fontSize
            },
            11, 12, 13, 14);
        putMBEnneagramDisplays(460f, 21.55f, 
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(11, 150),
                new TeamChartsMBPrefDisplay(15, 101), 
                new TeamChartsMBAnimalsDisplay(10),
                new TeamChartsEnnScoreDisplay(9), 
                new TeamChartsTeamNameDisplay(16, 95) // fontSize
            },
            15, 16, 17, 18, 19, 20); 
        putMBEnneagramDisplays(490.2f, 18.73f, 
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(11, 200),
                new TeamChartsMBPrefDisplay(14, 103), 
                new TeamChartsMBAnimalsDisplay(10),
                new TeamChartsEnnScoreDisplay(9), 
                new TeamChartsTeamNameDisplay(16, 75)
            },
            21, 22, 23, 24, 25);
    }
    
    private static void putMBEnneagramDisplays(float y, float ySpacing,
                                               AbstractTeamChartsDisplay[] displays, 
                                               Integer... teamSizes) {
        // Set the shared y and ySpacing attributes on all displays:
        for(AbstractTeamChartsDisplay display: displays) {
            display.setY(y);
            display.setYSpacing(ySpacing);
        }
        teamSizesToDisplays.put(Arrays.asList(teamSizes), Arrays.asList(displays));
    }
    
    public static TeamChartsNameDisplay getNameDisplay(int teamSize) 
    throws Exception {
        return (TeamChartsNameDisplay) getDisplay(teamSize, 0, "name");         
    }
    
    public static TeamChartsMBPrefDisplay getPreferencesDisplay(int teamSize) 
    throws Exception {
        return (TeamChartsMBPrefDisplay) getDisplay(teamSize, 1, "preference");    
    }   
    
    public static TeamChartsMBAnimalsDisplay getAnimalsDisplay(int teamSize) 
    throws Exception {
        return (TeamChartsMBAnimalsDisplay) getDisplay(teamSize, 2, "animal");    
    } 
    
    public static TeamChartsEnnScoreDisplay getEnneagramDisplay(int teamSize) 
    throws Exception {
        return (TeamChartsEnnScoreDisplay) getDisplay(teamSize, 3, "Enneagram");    
    } 
    
    public static TeamChartsTeamNameDisplay getTeamNameDisplay(int teamSize) 
    throws Exception {
        return (TeamChartsTeamNameDisplay) getDisplay(teamSize, 4, "team name");         
    }
    
    private static AbstractTeamChartsDisplay getDisplay(int teamSize, int index, String label) 
    throws Exception {
        for(Map.Entry<List<Integer>, List<AbstractTeamChartsDisplay>> entry: teamSizesToDisplays.entrySet()) {
            List<Integer> teamSizes = entry.getKey();
            if(teamSizes.contains(teamSize))
                return entry.getValue().get(index);
        }
        
        throw new Exception("No Myers-Briggs "+label+"-display settings exist for team size " + teamSize);          
    }  
}
