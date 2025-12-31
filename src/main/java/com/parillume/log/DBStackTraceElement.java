/*
 * Copyright(c) 2025 Parillume, All rights reserved worldwide
 */
package com.parillume.log;

import lombok.Data;

/**
 *
 * @author Tom
 */
@Data
public class DBStackTraceElement {
    private String fileName;
    private String methodName;
    private int lineNumber;
    
    public DBStackTraceElement() {}
    
    public DBStackTraceElement(StackTraceElement element) {
        setFileName(element.getFileName());
        setMethodName(element.getMethodName());
        setLineNumber(element.getLineNumber());
    }
}
