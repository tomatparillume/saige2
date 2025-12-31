/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.security.repository;

import com.parillume.db.model.DBPermissibleActions;
import java.util.Optional;
import javax.persistence.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author tmargolis
 * @author tom@parillume.com
 */
@Repository
@Cacheable(false)
public interface PermissibleActionsRepository extends JpaRepository<DBPermissibleActions, String> {
    public Optional<DBPermissibleActions> findByUsername(String username);
    public Optional<DBPermissibleActions> findByCompanyId(String companyId);
    public Optional<DBPermissibleActions> findByUsernameAndCompanyId(String username, String companyId);
    public Optional<DBPermissibleActions> findByUsernameOrCompanyId(String username, String companyId);
}
