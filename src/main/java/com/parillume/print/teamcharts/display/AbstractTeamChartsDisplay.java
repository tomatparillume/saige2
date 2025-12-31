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
public abstract class AbstractTeamChartsDisplay {
    private float y;
    private float ySpacing;
    private int size;
    
    public AbstractTeamChartsDisplay() {}
    
    public AbstractTeamChartsDisplay(float y, float ySpacing, int size) {
        setY(y);
        setYSpacing(ySpacing);
        setSize(size);
    }
    
    public abstract int getX();    
    
    public int adjustSize(String name) {
        return getSize(); // No-op by default
    }
    
    public int adjustX(int x) {
        return x; // No-op by default
    }
    
    public float adjustY(float y, int lineNumber) {
        y -= ySpacing;           
        return y;
    }   
}
