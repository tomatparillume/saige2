/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print;

import com.parillume.util.StringUtil;
import java.util.HashMap;
import java.util.Map;

public enum VariablesArg {
    teamname,
    companyid;

    public static Map<VariablesArg,String> getVariablesArgs(Map<String,String> argsMap) 
    throws Exception {
        Map<VariablesArg,String> argToValue = new HashMap<>();  
        
        for(VariablesArg arg: VariablesArg.values()) {
            String value = argsMap.get(arg.name());
            if(!StringUtil.isEmpty(value))
                argToValue.put(arg, value);
        }
        
        return argToValue;
    }
}