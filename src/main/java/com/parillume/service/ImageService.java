/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.service;

import com.parillume.print.display.DBImage;
import com.parillume.db.repository.ImageRepository;
import com.parillume.util.model.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public void upsertImage(DBImage image) throws IOException {
        imageRepository.save(image);
    }
    
    public DBImage getImage(Long id) {
        return imageRepository.findById(id).orElse(null);
    }
    
    public DBImage getImage(String companyId, ImageType imageType) {
        return imageRepository.findByCompanyIdAndImageType(companyId, imageType);
    }
    
    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }
    
    public void deleteCompanyImages(String companyId) {
        for(ImageType type: ImageType.values()) {
            DBImage image = getImage(companyId, type);
            if(image != null)
                deleteImage(image.getId());
        }
    }
}
