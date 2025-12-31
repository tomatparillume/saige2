package com.parillume.db.repository;

import com.parillume.db.model.DBCompany;
import javax.persistence.Cacheable;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
@Repository
@Cacheable(false)
public interface CompanyRepository extends JpaRepository<DBCompany, String> {    
    
    DBCompany findByCompanyId(String companyId);
    
    @Transactional
    @Modifying
    @Query(value="delete from company c where c.company_id = ?1", nativeQuery=true)
    void deleteByCompanyId(String companyId); 
    
    @Transactional
    @Modifying
    @Query(value="update company c set c.companyjson = ?1 where c.company_id = ?2", nativeQuery=true)
    void updateCompany(String companyJSON, String companyId);
}