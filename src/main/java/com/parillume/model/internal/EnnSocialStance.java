/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public enum EnnSocialStance {
    ASSERTIVE("Assertive"),
    ALIGNING("Aligning"),
    WITHDRAWING("Withdrawing");

    private String label;

    private EnnSocialStance(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }  
}  