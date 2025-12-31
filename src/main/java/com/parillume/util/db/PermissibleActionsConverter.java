/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.parillume.security.PermissibleAction;
import com.parillume.util.JSONUtil;
import com.parillume.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

/**
 * Converts between a list of PermissibleActions and a JSON string of that list.
 * @author tmargolis
 * @author tom@parillume.com
 */
@Component
public class PermissibleActionsConverter extends AbstractLoggingConverter
                                         implements AttributeConverter<List<PermissibleAction>, String> {

    @Override
    public String convertToDatabaseColumn(List<PermissibleAction> actions) {
        String ret = "";
        
        if(actions != null) {
            try {
                ret = JSONUtil.toJSONNoEscape( actions.stream().map(a -> a.name()).collect(Collectors.toList()) );
            } catch(Exception exc) {
                log(exc);
            }
        }
        
        return ret; 
    }

    @Override
    public List<PermissibleAction> convertToEntityAttribute(String actionsJSON) {
        List<PermissibleAction> ret = new ArrayList<>();
        
        if(!StringUtil.isEmpty(actionsJSON)) {
            try {
                ret = JSONUtil.fromJSON(actionsJSON, new TypeReference<List<PermissibleAction>>(){});
            } catch(Exception exc) {
                log(exc);
            }
        }
        
        return ret;
    }
}
