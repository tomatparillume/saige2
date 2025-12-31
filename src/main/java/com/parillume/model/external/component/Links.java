/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.external.component;

import com.parillume.util.StringUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 *
 *  * @author tom@parillume.com
 */
@Data
public class Links {
    private Map<LinkType, String> typeToLink = new HashMap<>();
    
    public void put(LinkType type, String link) {
        typeToLink.put(type, link);
    }
    public String get(LinkType type) {
        String foundType = typeToLink.get(type);
        if(StringUtil.isEmpty(foundType))
            foundType = "";
        return foundType;
    }
}
