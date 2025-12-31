/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class JSONUtil {   
    public static ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
    }       
    public static ObjectMapper MAPPER_NO_ESCAPE = new ObjectMapper();
    static {
        MAPPER_NO_ESCAPE.configure(SerializationFeature.INDENT_OUTPUT, true);
        MAPPER_NO_ESCAPE.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
    }   
    public static ObjectMapper MAPPER_ONE_LINE_NO_ESCAPE = new ObjectMapper();
    static {
        MAPPER_ONE_LINE_NO_ESCAPE.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
    } 
    
    public static final String EMPTY_JSON = (new JSONObject()).toString();
    
    public static String toJSON(Object o)  throws Exception {
        return MAPPER.writer().writeValueAsString(o);        
    }
    public static String toJSONNoEscape(Object o)  throws Exception {
        return MAPPER_NO_ESCAPE.writer().writeValueAsString(o);        
    }
    public static String toJSONOneLine(Object o)  throws Exception {
        return MAPPER_ONE_LINE_NO_ESCAPE.writer().writeValueAsString(o);        
    }
    public static JSONArray toJSONArray(Object o)  throws Exception {
        return new JSONArray( MAPPER_NO_ESCAPE.writer().writeValueAsString(o) ); 
    }
    public static JSONObject toJSONObject(Object o)  throws Exception {
        return new JSONObject( MAPPER_NO_ESCAPE.writer().writeValueAsString(o) ); 
    }
    
    public static <T> T fromJSON(String json, Class<T> outputClass) throws Exception {
        return MAPPER.readValue(json, outputClass);
    }   
    
    public static <T> T fromJSON(String json, TypeReference<T> reference) throws Exception {
        return MAPPER.readValue(json, reference);
    }     
}
