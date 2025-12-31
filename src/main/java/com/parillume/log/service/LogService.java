/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.log.service;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
import com.parillume.db.repository.LogRepository;
import com.parillume.log.LogRecord;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Data
@Service
public class LogService {       
    
    private static final int MAX_COUNT = 1000;
    private static final long MAX_AGE = 1000L * 60L * 60L * 24L * 30L;
    
    @Autowired
    private LogRepository logRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    public List<LogRecord> getLogs() {
        List<LogRecord> logRecords = logRepository.findAll();
        Collections.sort(logRecords);
        return logRecords;
    }
    
    public void log(LogRecord logRecord) {
        synchronized(LogService.class) {
            try {
                logRepository.save(logRecord);
            } catch(Exception exc) {}
        }
    }
    
    /**
     * Runs every 12 hours
     */
    @Transactional
    @Scheduled(fixedRate = 1000*60*60*12)
    public void purgeLogRecords() {
        synchronized(LogService.class) {
            long oldestAllowableTimestamp = System.currentTimeMillis() - MAX_AGE;
            logRepository.deleteByLogTimestampLessThan(oldestAllowableTimestamp);

            if(logRepository.count() > MAX_COUNT) {
                Long maxEarliestTimestamp = (Long) entityManager.createQuery(
                    "SELECT e.logTimestamp FROM "+LogRecord.DBTABLE+" e ORDER BY e.logTimestamp DESC"
                )
                .setMaxResults(MAX_COUNT)
                .getResultList()
                .get(MAX_COUNT-1); // Get the last record list

                // Step 2: Delete all records where the ID is less than the latest 1000th record
                entityManager.createQuery(
                    "DELETE FROM "+LogRecord.DBTABLE+" e WHERE e.logTimestamp < :maxEarliestTimestamp"
                )
                .setParameter("maxEarliestTimestamp", maxEarliestTimestamp)
                .executeUpdate();                
            }
        }
    }   
}
