/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.display;

import com.parillume.util.FileUtil;
import com.parillume.util.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;

/**
 * References and retrieves images in the src/main/resources/images directory.
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class DiskImage implements ImageIF {
        
    private static List<DiskImage> images = new ArrayList<>();
    static {
        images.add(new DiskImage(PARILLUME_LOGO, FileUtil.IMAGES_DIR, "parillume_logo.png"));
        images.add(new DiskImage(SAIGE_LOGO, FileUtil.IMAGES_DIR, "saige_logo.png"));
        images.add(new DiskImage(CLIENT_LOGO, FileUtil.TMP_DIR, "client_logo.png"));
        
        images.add(new DiskImage(STRENGTHCIRCLE_1, FileUtil.IMAGES_DIR, "strengthcircle_1.png"));
        images.add(new DiskImage(STRENGTHCIRCLE_2, FileUtil.IMAGES_DIR, "strengthcircle_2.png"));
        images.add(new DiskImage(STRENGTHCIRCLE_3, FileUtil.IMAGES_DIR, "strengthcircle_3.png"));
        images.add(new DiskImage(STRENGTHCIRCLE_4, FileUtil.IMAGES_DIR, "strengthcircle_4.png"));
        images.add(new DiskImage(STRENGTHCIRCLE_5, FileUtil.IMAGES_DIR, "strengthcircle_5.png"));
    }  

    private String imageId;
    private File directory;
    private String fileName;

    public DiskImage(String imageId, File directory, String fileName) {
        this.imageId = imageId;
        this.directory = directory;
        this.fileName = fileName;
    }
    @Override
    public String getFileName() {
        return fileName;
    }
    @Override
    public InputStream getImageStream() throws Exception {
        File f = new File(directory, fileName);
        return f.exists() ?
               // From file system:
               new FileInputStream(f) :
               // From resources/ dir inside jar:
               getClass().getClassLoader().getResourceAsStream(directory.getName() + "/" + fileName);
    }

    public static DiskImage getImage(String imageId) {
        Optional<DiskImage> optImage = images.stream()
                                             .filter(img -> StringUtil.nullEquals(img.getImageId(), imageId))
                                             .findFirst();

        if(!optImage.isPresent())
            throw new RuntimeException("Image id "+ imageId + " is not associated with an image");
        
        return optImage.get();
    }
    
    public static DiskImage getStrengthCircle(int strengthNumber /* 1-5 */) {
        String id = "";
        
        switch(strengthNumber) {
            case 1: id = STRENGTHCIRCLE_1; break;
            case 2: id = STRENGTHCIRCLE_2; break;
            case 3: id = STRENGTHCIRCLE_3; break;
            case 4: id = STRENGTHCIRCLE_4; break;
            case 5: id = STRENGTHCIRCLE_5; break;
            default:
        }
        
        return getImage(id);
    }
}