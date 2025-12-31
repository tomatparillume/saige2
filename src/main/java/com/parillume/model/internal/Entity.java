/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model.internal;

import com.parillume.model.CompanyModel;
import com.parillume.util.model.EntityType;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public interface Entity {
    public EntityType getEntityType();
    
    default void validateFields(CompanyModel companyModel) throws Exception {
        return;
    }
}
