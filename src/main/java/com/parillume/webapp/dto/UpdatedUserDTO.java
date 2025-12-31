/*
 * Copyright(c) 2024, Billtrust Inc., All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.parillume.model.external.User;
import com.parillume.security.dto.SessionDTO;
import lombok.Data;

/**
 * Represents an updated User to be returned to the UI; includes a SessionDTO
 * because the User's credentials may have changed.
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class UpdatedUserDTO {
    SessionDTO sessionDTO;
    User user;
    
    public UpdatedUserDTO( SessionDTO sessionDTO, User user) {
        setSessionDTO(sessionDTO);
        setUser(user);
    }
}
