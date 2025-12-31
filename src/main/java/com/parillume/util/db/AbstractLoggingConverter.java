/*
 * Copyright(c) 2025 Parillume, All rights reserved worldwide
 */
package com.parillume.util.db;

import com.parillume.log.LogRecord;
import com.parillume.log.service.LogService;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Tom
 */
public abstract class AbstractLoggingConverter {    
    /**
     * Because AttributeConverters are constructed by JPA, they can't directly
     * @Autowire another service (e.g. LogService). So we jump through hoops to
     * access LogService:
     */
    private LogService logService;
    @Autowired
    private ApplicationContext applicationContext;
    @PostConstruct
    public void init() { logService = applicationContext.getBean(LogService.class); }
    
    protected void log(Exception exc) {
        log(new LogRecord(exc));
    }
    protected void log(LogRecord logRecord) {
        logService.log(logRecord);
    }
}
