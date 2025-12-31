/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util.model;

/**
 * A category of data that can be CRUD'ed as an independent group.
 *  * @author tom@parillume.com
 */
public enum EntityType {
    // Internally-defined data:
    ASSESSMENT,
    ASSESSMENT_CATEGORY_TYPE,
    ASSESSMENT_CATEGORY,
    ASSESSMENT_RESULT,
    ROLE_TYPE,
    TERM,
    
    // Externally-defined data:
    COMPANY,
    ROLE, 
    USER,
    TEAM;
}
