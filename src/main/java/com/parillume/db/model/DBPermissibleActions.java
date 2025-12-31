/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.db.model;

import com.parillume.security.PermissibleAction;
import com.parillume.util.db.PermissibleActionsConverter;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
@Entity
@Table(name = "permissibleactions")
public class DBPermissibleActions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "companyid")
    private String companyId;
    
    @Convert(converter = PermissibleActionsConverter.class)
    @Column(name = "actions")
    private List<PermissibleAction> actions;
    
    public DBPermissibleActions() {}
    
    public DBPermissibleActions(String username, String companyId, List<PermissibleAction> actions) {
        setUsername(username);
        setCompanyId(companyId);
        setActions(actions);
    }
}
