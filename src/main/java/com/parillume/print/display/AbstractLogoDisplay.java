/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.display;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public abstract class AbstractLogoDisplay {
    public enum Position {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;
    }
    
    public abstract Position getPosition(String imageId);
    public abstract int getW(String imageId);
    public abstract int getH(String imageId);
}
