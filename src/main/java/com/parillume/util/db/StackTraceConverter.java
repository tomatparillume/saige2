/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.parillume.log.DBStackTraceElement;
import com.parillume.util.JSONUtil;
import com.parillume.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Component
public class StackTraceConverter extends AbstractLoggingConverter
                                 implements AttributeConverter<List<DBStackTraceElement>, String> {
    
    @Override
    public String convertToDatabaseColumn(List<DBStackTraceElement> stackTrace) {
        try {
            return stackTrace != null ?
                   JSONUtil.toJSON(stackTrace) :
                   JSONUtil.EMPTY_JSON;
        } catch(Exception exc) {
            return JSONUtil.EMPTY_JSON;
        }
    }

    @Override
    public List<DBStackTraceElement> convertToEntityAttribute(String stackTraceJSON) {
        try {
            return !StringUtil.isEmpty(stackTraceJSON) ?
                   JSONUtil.fromJSON(stackTraceJSON, new TypeReference<List<DBStackTraceElement>>(){}) :
                   new ArrayList();
        } catch(Exception exc) {            
            return new ArrayList();
        }
    }
}
