package com.parillume.db.repository;

import com.parillume.log.LogRecord;
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
public interface LogRepository extends JpaRepository<LogRecord, String> { 
    void deleteByLogTimestampLessThan(long earliestTimestamp);
}