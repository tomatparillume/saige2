package com.parillume.util.db;

import com.parillume.model.internal.AssessmentType;
import com.parillume.util.JSONUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;

public class AssessmentTypesConverter extends AbstractLoggingConverter
                                      implements AttributeConverter<List<AssessmentType>, String> {
    
    @Override
    public String convertToDatabaseColumn(List<AssessmentType> types) {
        String json = null;

        List<String> typeStrings = types.stream()
                                        .map(t -> t.name())
                                        .collect(Collectors.toList());
        try {
            json = JSONUtil.toJSON(typeStrings);
        } catch (Exception exc) {
            log(exc);
        }

        return json;
    }

    @Override
    public List<AssessmentType> convertToEntityAttribute(String s) {
        List<AssessmentType> typeList = new ArrayList<>();
        
        try {
            List<String> stringList = JSONUtil.fromJSON(s, List.class);
            typeList = stringList.stream()
                                 .map(str -> AssessmentType.valueOf(str))
                                 .collect(Collectors.toList());
        } catch (Exception exc) {
            log(exc);
        }
        
        return typeList;
    }
}
