/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.print.input;

import com.parillume.model.external.User;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class UserImportKey implements DataImportKeyIF {
    private User user;
    
    public UserImportKey(User user) {
        setUser(user);
    }
    
    @Override
    public String getName() {
        return user.getId();
    }
    
    @Override    
    public String getOutputFileName() {
        return getUser().getNameFirst() + "_" + getUser().getNameLast();
    }
}
