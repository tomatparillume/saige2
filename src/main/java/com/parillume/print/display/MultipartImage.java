/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.display;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * References and retrieves images in the src/main/resources/images directory.
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class MultipartImage implements ImageIF {
    
    private String imageId;
    private InputStream imageStream;
    
    public MultipartImage(String imageId, byte[] imageBytes) {
        setImageId(imageId);
        setImageStream( new ByteArrayInputStream(imageBytes) );
    }
    public MultipartImage(String imageId, MultipartFile file) 
    throws Exception {
        setImageId(imageId);
        setImageStream( new BufferedInputStream(file.getInputStream()) );
    }
    
    @Override
    public String getFileName() { return ""; }
}