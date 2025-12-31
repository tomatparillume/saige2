/*
 * Copyright(c) 2024, Billtrust Inc., All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.parillume.util.StringUtil;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class CredentialsDTO {
    private String username;
    private String password;

    public CredentialsDTO() {}
    
    public CredentialsDTO(String username, String password) {
        setUsername(username);
        setPassword(password);
    }
    
    @JsonIgnore
    public boolean isPopulated() {
        return !StringUtil.isEmpty(username) && !StringUtil.isEmpty(password);
    }
}
