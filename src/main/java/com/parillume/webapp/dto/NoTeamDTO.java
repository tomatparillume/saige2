/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import com.parillume.model.external.Team;
import com.parillume.model.external.User;
import java.util.List;
import lombok.Data;


/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class NoTeamDTO extends TeamDTO {
    public static final String NOTEAM_ID = "no-team";
    public static final String NOTEAM_NAME = "No team";
    
    public NoTeamDTO(List<User> noTeamUsers) {
        setUsers(noTeamUsers);
    }
    
    /**
     * We hide the NoTeam from the DB, and thus there is no model object for the NoTeam.
     */
    public Team toNoTeam() {
        Team noTeam = new Team();
        noTeam.setId(getId());
        noTeam.setName(getLabel());
        noTeam.setDescription("People not associated with a team");
        return noTeam;
    }
    
    @Override
    public void setId(String id) {
        // No-op
    }
    
    @Override
    public String getId() {
        return NOTEAM_ID;
    }
    
    @Override
    public void setLabel(String label) {
        // No-op
    }
    
    @Override
    public String getLabel() {
        return NOTEAM_NAME;
    }
}
