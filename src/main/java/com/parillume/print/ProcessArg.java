/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print;

import com.parillume.util.StringUtil;
import java.util.Arrays;
import java.util.Map;

public enum ProcessArg {
    onesheets("one-sheets"), // default
    multisheets("multi-sheets"),
    teamcharts("team charts");

    private String label;
    private ProcessArg(String label) {
        this.label = label;
    }            
    
    public String getLabel() { return label; }
    
    public static ProcessArg getProcessArg(Map<String,String> argsMap) 
    throws Exception {
        String process = argsMap.get("process");
        try {
            return !StringUtil.isEmpty(process) ?
                   valueOf(process.toLowerCase()) :
                   onesheets;
        } catch(IllegalArgumentException e) {
            throw new Exception( process + " is not a valid process=[value]; " +
                                 "valid values are " + Arrays.asList(ProcessArg.values()) );
        }
    }
}