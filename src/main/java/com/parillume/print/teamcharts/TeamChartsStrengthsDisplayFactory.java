/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.teamcharts;

import com.parillume.print.teamcharts.display.AbstractTeamChartsDisplay;
import com.parillume.print.teamcharts.display.TeamChartsNameDisplay;
import com.parillume.print.teamcharts.display.TeamChartsStrengthCircleDisplay;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class TeamChartsStrengthsDisplayFactory {
    
    private static Map<List<Integer>, List<AbstractTeamChartsDisplay>> 
                    teamSizesToDisplays = new HashMap<>();  
    static {     
        putStrengthDisplays(159f, 27.55f, // y, ySpacing
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(13, 200), // fontSize, nameWrapWidth
                new TeamChartsStrengthCircleDisplay(137, 18.75f) // x, circleSpacing
            },
            2, 3, 4); // team size(s)
        putStrengthDisplays(188.9f, 27.4f, // y, ySpacing
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(13, 150),
                new TeamChartsStrengthCircleDisplay(137, 18.75f)
            },
            5, 6); // team size(s)
        putStrengthDisplays(346f, 27.35f, // y, ySpacing
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(13, 150),
                new TeamChartsStrengthCircleDisplay(137, 18.8f)
            },
            7, 8, 9, 10);
        putStrengthDisplays(370.9f, 23.9f, // y, ySpacing
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(13, 150),
                new TeamChartsStrengthCircleDisplay(137, 18.75f)
            },
            11, 12, 13, 14);
        putStrengthDisplays(378.5f, 15.9f, // y, ySpacing
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(11, 200),
                new TeamChartsStrengthCircleDisplay(137, 18.75f)
            },
            15, 16, 17, 18, 19, 20);
        putStrengthDisplays(396.5f, 15.85f, // y, ySpacing
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(11, 200),
                new TeamChartsStrengthCircleDisplay(135, 18.75f)
            },
            21, 22, 23);
        putStrengthDisplays(407.2f, 15.92f, // y, ySpacing
            new AbstractTeamChartsDisplay[]{
                new TeamChartsNameDisplay(11, 200),
                new TeamChartsStrengthCircleDisplay(135, 18.75f)
            },
            24, 25);
    }
    
    private static void putStrengthDisplays(float y, float ySpacing,
                                            AbstractTeamChartsDisplay[] displays, 
                                            Integer... teamSizes) {
        // Set the shared y and ySpacing attributes onall displays:
        for(AbstractTeamChartsDisplay display: displays) {
            display.setY(y);
            display.setYSpacing(ySpacing);
        }
        teamSizesToDisplays.put(Arrays.asList(teamSizes), Arrays.asList(displays));
    }
    
    public static TeamChartsNameDisplay getStrengthNameDisplay(int teamSize) 
    throws Exception {
        return (TeamChartsNameDisplay) getDisplay(teamSize, 0, "name");         
    }
    public static TeamChartsStrengthCircleDisplay getStrengthCircleDisplay(int teamSize) 
    throws Exception {
        return (TeamChartsStrengthCircleDisplay) getDisplay(teamSize, 1, "strengths circle");         
    }
    
    private static AbstractTeamChartsDisplay getDisplay(int teamSize, int index, String label) 
    throws Exception {
        for(Map.Entry<List<Integer>, List<AbstractTeamChartsDisplay>> entry: teamSizesToDisplays.entrySet()) {
            List<Integer> teamSizes = entry.getKey();
            if(teamSizes.contains(teamSize))
                return entry.getValue().get(index);
        }
        
        throw new Exception("No Strengths "+label+"-display settings exist for team size " + teamSize);          
    }  
}
