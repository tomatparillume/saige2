package com.parillume.util.db;

import com.parillume.util.JSONUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.AttributeConverter;

public class StringListConverter extends AbstractLoggingConverter
                                 implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        String json = null;

        try {
            json = JSONUtil.toJSON(strings);
        } catch (Exception exc) {
            log(exc);
        }

        return json;
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        List<String> list = new ArrayList<>();
        
        try {
            list = JSONUtil.fromJSON(s, List.class);
        } catch (Exception exc) {
            log(exc);
        }
        
        return list;
    }
}
