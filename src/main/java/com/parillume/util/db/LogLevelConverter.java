/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util.db;

import com.parillume.util.StringUtil;
import java.lang.System.Logger.Level;
import javax.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Component
public class LogLevelConverter implements AttributeConverter<Level, String> {

    @Override
    public String convertToDatabaseColumn(Level level) {
        return level != null ? level.name() : "";
    }

    @Override
    public Level convertToEntityAttribute(String levelStr) {
        return !StringUtil.isEmpty(levelStr) ?
               Level.valueOf(levelStr.toUpperCase()) :
               null;
    }
}
