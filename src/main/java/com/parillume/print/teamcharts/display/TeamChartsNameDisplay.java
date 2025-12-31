/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.teamcharts.display;

import com.parillume.print.teamcharts.TeamChartsWriter;
import lombok.Data;
import org.apache.commons.lang3.text.WordUtils;

/** 
 * Defines configuration for writing names to the Myers-Briggs rows of a team chart.
 * @author tmargolis
 * @author tom@parillume.com
 * See TeamChartsWriter
 */
@Data
public class TeamChartsNameDisplay extends AbstractTeamChartsDisplay {
    private int nameWrapWidth;       
    
    public TeamChartsNameDisplay(int fontSize, int nameWrapWidth) {
        setSize(fontSize);
        setNameWrapWidth(nameWrapWidth);
    }
    
    @Override
    public int getX() {
        return 67;
    }
    
    // Shrink the name font size if the name is too long
    @Override
    public int adjustSize(String name) {
        int fontSize = getSize();
        int wrapWidth = this.nameWrapWidth / fontSize;
        
        String[] lines = WordUtils.wrap(name, wrapWidth, "\n", true).split("\\r?\\n");            
        if(lines.length > 1)
            fontSize = (int) (fontSize * 0.9);
        
        return fontSize;
    }
}
