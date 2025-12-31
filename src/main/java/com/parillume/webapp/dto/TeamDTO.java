/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.parillume.model.external.Team;
import com.parillume.model.external.User;
import com.parillume.util.Constants;
import com.parillume.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;


/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class TeamDTO extends AbstractWebAppDTO {
    private List<User> users = new ArrayList<>();
    
    public TeamDTO() {}
    
    public TeamDTO(Team team) {
        setId(team.getId());
        setLabel(team.getName());
    }
    
    public void setUsers(List<User> users) {
        for(User user: users) {
            user.setPassword( !StringUtil.isEmpty(user.getPassword()) ?
                              Constants.PASSWORD_MASK :
                              "" );
        }
        this.users = users;
    }
}
