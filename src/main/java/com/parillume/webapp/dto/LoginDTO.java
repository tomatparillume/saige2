/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.parillume.model.external.User;
import com.parillume.security.dto.SessionDTO;
import lombok.Data;

/**
 * See AssessmentResultsDTO, which contains multiple AssessmentDTOs
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class LoginDTO {
    private SessionDTO sessionDTO;
    private User user;
    
    public LoginDTO() {}
    
    public LoginDTO(SessionDTO sessionDTO, User user) {
        setSessionDTO(sessionDTO);
        setUser(user);
    }
}
