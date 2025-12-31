/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util.db;

import com.parillume.util.StringUtil;
import com.parillume.util.model.ImageType;
import javax.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Component
public class ImageTypeConverter implements AttributeConverter<ImageType, String> {

    @Override
    public String convertToDatabaseColumn(ImageType imageType) {
        return imageType != null ? imageType.name() : "";
    }

    @Override
    public ImageType convertToEntityAttribute(String imageTypeStr) {
        return !StringUtil.isEmpty(imageTypeStr) ?
               ImageType.valueOf(imageTypeStr.toUpperCase()) :
               null;
    }
}
