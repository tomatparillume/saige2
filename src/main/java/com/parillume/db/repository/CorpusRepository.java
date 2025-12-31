package com.parillume.db.repository;

import com.parillume.db.model.DBCorpus;
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
public interface CorpusRepository extends JpaRepository<DBCorpus, String> {    
    
    DBCorpus findByVersion(String version);
    
    @Transactional
    @Modifying
    @Query(value="delete from corpus c where c.version = ?1", nativeQuery=true)
    void deleteByCorpusVersion(String version); 
    
    @Transactional
    @Modifying
    @Query(value="update corpus c set c.corpusjson = ?1 where c.version = ?2", nativeQuery=true)
    void updateCorpus(String corpusJSON, String version);
}