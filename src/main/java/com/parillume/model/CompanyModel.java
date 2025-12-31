/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.parillume.model.external.Company;
import com.parillume.model.external.Role;
import com.parillume.model.external.Team;
import com.parillume.model.external.User;
import com.parillume.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
public class CompanyModel {
    
    private Company company;
    private List<String> managedCompanyIds = new ArrayList<>();
    private List<Team> teams = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    
    @JsonIgnore
    public void removeUser(String userId) {
        users.removeIf( u -> StringUtil.nullEquals(userId, u.getId()) );
    }
    
    @JsonIgnore
    public List<User> getNoTeamUsers() {
        return collectUsers(false); 
    }
    
    @JsonIgnore
    public List<User> getTeamUsers() {
        return collectUsers(true); 
    }
    
    public CompanyModel clone() {
        CompanyModel clone = new CompanyModel();
        clone.setManagedCompanyIds(new ArrayList(getManagedCompanyIds()));
        clone.setCompany(getCompany().clone());
//        clone.setTeams(getTeams().stream()
//                                 .map(t -> t.clone())
//                                 .collect(Collectors.toList()));
//        clone.setRoles(getRoles().stream()
//                                 .map(r -> r.clone())
//                                 .collect(Collectors.toList()));
//        clone.setUsers(getUsers().stream()
//                                 .map(u -> u.clone())
//                                 .collect(Collectors.toList()));
        return clone;
    }
    
    private List<User> collectUsers(boolean onTeam) {
        return getUsers().stream()
                         .filter( u -> 
                                  onTeam != (u.getTeamIdToManagerId().isEmpty() && 
                                             u.getTeamIdToRoleId().isEmpty()) 
                         )
                         .collect(Collectors.toList()); 
    }    
}