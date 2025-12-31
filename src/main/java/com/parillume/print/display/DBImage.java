/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.display;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.parillume.util.db.ImageTypeConverter;
import com.parillume.util.model.ImageType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
@Entity
@Table(name = "image")
public class DBImage implements ImageIF {

    /************************* ImageIF (non-DB) methods ************************/
    @JsonIgnore
    @Override
    public String getImageId() {
        return getId() != null ? String.valueOf(getId()) : null;
    }
    @JsonIgnore
    @Override
    public String getFileName() {
        return getImageName();
    }
    @JsonIgnore
    @Override
    public InputStream getImageStream() throws Exception {
        return new ByteArrayInputStream(getImageBytes());
    }
    /***************************************************************************/
    
    public DBImage() {}
    
    public DBImage(String companyId, String imageName, ImageType imageType,
                   byte[] imageBytes,
                   Long replacementId) {
        setCompanyId(companyId);
        setImageName(imageName);
        setImageType(imageType);
        setImageBytes(imageBytes);
        setId(replacementId); // May be null
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "companyId")
    private String companyId;        
    
    @Column(name = "imageName")
    private String imageName;
    
    @Convert(converter = ImageTypeConverter.class)
    @Column(name = "imageType")
    private ImageType imageType;
    
    @Lob
    @Column(name = "imageBytes")
    private byte[] imageBytes;
}
