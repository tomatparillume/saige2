/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

import org.json.JSONObject;


/**
 *
 *  * @author tom@parillume.com
 */
public enum AssessmentType {
    CliftonStrengths("CliftonStrengths", "®"),
    MyersBriggs("Myers-Briggs", "®"),
    Enneagram("Enneagram");
    
    private String name;
    private String trademarkSymbol;
    
    private AssessmentType(String name) {
        this(name, "");
    }
    private AssessmentType(String name, String trademarkSymbol) {
        this.name = name;
        this.trademarkSymbol = trademarkSymbol;
    }
    
    public String getName() {
        return name;
    }
    public String getTrademarkSymbol() {
        return trademarkSymbol;
    }
    public String getLabel() {
        return getName() + getTrademarkSymbol();
    }
    
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", getName());
        json.put("type", name());
        return json;
    }    
}
