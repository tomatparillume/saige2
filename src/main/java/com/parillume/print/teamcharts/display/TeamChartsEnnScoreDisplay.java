/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.teamcharts.display;

import com.parillume.print.teamcharts.TeamChartsWriter;
import com.parillume.model.score.EnneagramScore;
import lombok.Data;

/** 
 * Defines configuration for writing Enneagram scores to the Myers-Briggs rows
 * of a team chart.
 * @author tmargolis
 * @author tom@parillume.com
 * See TeamChartsWriter
 */
@Data
public class TeamChartsEnnScoreDisplay extends AbstractTeamChartsDisplay {     
    
    public TeamChartsEnnScoreDisplay(int fontSize) {
        setSize(fontSize);
    }
    
    @Override
    public int getX() {
        return 712;
    }
    @Override
    public float getY() {
        return super.getY() - 1.5f;
    }
    
    public float getLabelY() {
        return getY() + 6;
    }
    public float getBurningQuestionY() {
        return getLabelY() - (getSize() - 1);
    }
    
    @Override
    public int adjustSize(String s) {
        return EnneagramScore.isLabel(s) ?
               // Label font:
               getSize() :
               // Smaller font for BQ, etc.
               (int) (getSize() * 0.8); 
    }    
}
