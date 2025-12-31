/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.lexicon;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 *
 *  * @author tom@parillume.com
 */
@Data
public abstract class Termible {    
    private Map<String, String> termIdToValue = new HashMap<>();
    
    public void addTerm(Term term, String value) {
        getTermIdToValue().put(term.getId(), value);
    }
    
    public String getTermValue(Term term) {
        return getTermIdToValue().get(term.getId());
    }
}