/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.security.repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tmargolis
 * @author tom@parillume.com
 */
public class SessionRepository { 
    
    //TODO: Replace with a DB table:
    private static Map<String,Long> sessions = new HashMap<>();   
    
    public static Map<String,Long> getAll() {
        return sessions;
    }
    
    public static void setAll(Map<String,Long> newSessions) {
        synchronized(SessionRepository.class) {
            sessions.clear();
            sessions.putAll(newSessions);
        }
    }
    
    public static Long getTimestamp(String sessionId) {
        return sessions.get(sessionId);
    }
    
    public static void put(String sessionId) {
        put(sessionId, System.currentTimeMillis());
    }
    
    public static void put(String sessionId, long currentTimestamp) {
        sessions.put(sessionId, currentTimestamp);
    }
    
    public static boolean has(String sessionId) {
        return sessions.containsKey(sessionId);
    }
    
    public static boolean remove(String sessionId) {
        return sessions.remove(sessionId) != null;
    }
}
