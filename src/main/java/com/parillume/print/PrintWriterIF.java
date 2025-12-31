/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.print;

import java.util.Map;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public interface PrintWriterIF<T> {
    default Integer getMaxUsers(ProcessArg process) {
        return Integer.MAX_VALUE;
    }
    
    public void write() throws Exception;
    
    /**
     * Each key is a file name
     * Each byte array returned represents a single PDF
     */
    public Map<String, byte[]> getBytes() throws Exception;
}
