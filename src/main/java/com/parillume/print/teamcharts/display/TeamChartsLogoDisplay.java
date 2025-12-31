/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.teamcharts.display;

import com.parillume.print.display.AbstractLogoDisplay;
import static com.parillume.print.display.AbstractLogoDisplay.Position;
import com.parillume.print.display.ImageIF;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class TeamChartsLogoDisplay extends AbstractLogoDisplay {
    private static Map<String, DisplayConfig> imageToDisplayConfig = new HashMap();
    static {
        // Top right of page:
        imageToDisplayConfig.put(ImageIF.PARILLUME_LOGO, 
                                 new DisplayConfig(130, 32, Position.TOP_RIGHT) );      
        // Top left of page:
        imageToDisplayConfig.put(ImageIF.CLIENT_LOGO, 
                                 new DisplayConfig(130, 32, Position.TOP_LEFT) );   
    }
    
    /**************************** SINGLETON SUPPORT ****************************/
    private static final TeamChartsLogoDisplay instance = new TeamChartsLogoDisplay();

    private TeamChartsLogoDisplay() {}
    
    public static TeamChartsLogoDisplay getInstance() {        
        return instance;
    }
    /***************************************************************************/
    
    @Override
    public Position getPosition(String imageId) {
        return imageToDisplayConfig.get(imageId).getPosition();
    }
    @Override
    public int getW(String imageId) {
        return imageToDisplayConfig.get(imageId).getW();
    }
    @Override
    public int getH(String imageId) {
        return imageToDisplayConfig.get(imageId).getH();
    }
    
    private static class DisplayConfig {
        private int w;
        private int h;
        private Position position;
        
        private DisplayConfig(int w, int h, Position position) {
            this.w = w;
            this.h = h;
            this.position = position;
        }

        public int getW() {
            return w;
        }
        
        public int getH() {
            return h;
        }

        public Position getPosition() {
            return position;
        }
    }
}
