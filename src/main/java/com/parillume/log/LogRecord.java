/*
 * Copyright(c) 2024, Billtrust Inc., All rights reserved worldwide
 */
package com.parillume.log;

import com.parillume.util.db.LogLevelConverter;
import com.parillume.util.db.StackTraceConverter;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
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
 * This class is sorted by its logTimestamp attribute value.
 * @author tmargolis
 * @author tom@parillume.com
 */
@Data
@Entity
@Table(name = "log")
public class LogRecord implements Comparable<LogRecord> {
    
    public static final String DBTABLE = "log"; 
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "logTimestamp")
    private Long logTimestamp;
    
    @Column(name = "message", columnDefinition="TEXT")
    private String message;
    
    @Convert(converter = LogLevelConverter.class)
    @Column(name = "level")
    private Level level;
    
    @Convert(converter = StackTraceConverter.class)
    @Column(name = "stackTrace", columnDefinition="TEXT")
    private List<DBStackTraceElement> stackTrace;

    public LogRecord() {
        setLogTimestamp(System.currentTimeMillis());
    }
 
    public LogRecord(String message) {
        this(message, Level.INFO);
    }
 
    public LogRecord(String message, Level level) {
        this();
        setMessage(message);
        setLevel(level);
    }
    
    public LogRecord(Exception exc, String message) {
        this(message, Level.ERROR);
        
        // Include only com.parillume stack trace entries:
        List<DBStackTraceElement> filteredElements = new ArrayList<>();
        for(StackTraceElement element: exc.getStackTrace()) {
            if(element.getClassName().startsWith("com.parillume"))
                filteredElements.add(new DBStackTraceElement(element));
            
            if(filteredElements.size() >= 5)
                break;
        }
        setStackTrace(filteredElements);
    }
    
    public LogRecord(Exception exc) {
        this(exc, exc.getMessage());
    }
    
    @Override
    public int compareTo(LogRecord logRecord) {
        return getLogTimestamp().compareTo(logRecord.getLogTimestamp());
    }    
}