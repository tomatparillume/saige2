/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.display;

import com.parillume.util.model.ImageType;
import java.io.InputStream;

/**
 * References and retrieves images in the src/main/resources/images directory.
 * @author tmargolis
 * @author tom@parillume.com
 */
public interface ImageIF {
    public static final String PARILLUME_LOGO = "PARILLUME_LOGO";
    public static final String SAIGE_LOGO = "SAIGE_LOGO";
    public static final String CLIENT_LOGO = ImageType.LOGO.name();
    
    public static final String STRENGTHCIRCLE_1 = "1";
    public static final String STRENGTHCIRCLE_2 = "2";
    public static final String STRENGTHCIRCLE_3 = "3";
    public static final String STRENGTHCIRCLE_4 = "4";
    public static final String STRENGTHCIRCLE_5 = "5";
    
    public String getImageId();
    public String getFileName();
    public InputStream getImageStream() throws Exception;
}