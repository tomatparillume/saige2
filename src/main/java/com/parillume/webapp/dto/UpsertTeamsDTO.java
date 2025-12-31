/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.webapp.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;


/**
 * @see index.html:editTeams()
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class UpsertTeamsDTO {
    private String newTeamName;
    private Map<String,String> teamIdToName = new HashMap<>();
    private List<String> teamIdsToDelete = new ArrayList<>();
}
