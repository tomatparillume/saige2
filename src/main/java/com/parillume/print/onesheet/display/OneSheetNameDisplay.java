/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.onesheet.display;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class OneSheetNameDisplay { 
    private boolean anatomySheet = false;
    
    public OneSheetNameDisplay(boolean anatomySheet) {
        this.anatomySheet = anatomySheet;
    }
    
    public int getNameFontSize() {  return anatomySheet ? 9 : 14;  }
    public float getNameX() {  return anatomySheet ? 571f : 482f;  }
    public float getNameY() {  return anatomySheet ? 428f : 582f;  }
}
