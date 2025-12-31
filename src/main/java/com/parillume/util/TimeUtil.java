/*
 * Copyright(c) 2023, Second Phase LLC., All rights reserved worldwide
 */
package com.parillume.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class TimeUtil {
    private static final DateFormat yyyyMMDDFormat = new SimpleDateFormat("yyyyMMdd");
    
    public static Date toDate(String yyyyMMDD) throws Exception {
        return yyyyMMDDFormat.parse(yyyyMMDD);
    }
    
    public static long getDaysAgo(long timeInMs) {
        Instant start = Instant.ofEpochMilli(timeInMs);
        Instant now   = Instant.ofEpochMilli(System.currentTimeMillis());
        return Duration.between(start,now).toDays();  
    }
    
    public static long getDaysSince1970(long timeInMs) {
        Instant start = Instant.ofEpochMilli(0);
        Instant now   = Instant.ofEpochMilli(timeInMs);
        return Duration.between(start,now).toDays();         
    }
    
    /**
     * Convert a daysAgo into the past into an epoch time representing the
     * start of the referenced (past) day.
     *
     * daysAgo represents FULL days, so if 1 day is requested at noon on
     * Thursday, this will return the startDayMs - 12:00:00.001 am - 
     * for Wednesday (yesterday).
     * 
     * If daysAgo is null, daysAgo=infinity and 0 is returned.
     */
    public static long getStartOfDayMs(int daysAgo) {
        return getStartOfDayMs((Integer)daysAgo);
    }
    public static long getStartOfDayMs(Integer daysAgo) {
        long startDayMs = 1L;
        if(daysAgo != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DATE, -1 * daysAgo);           
            startDayMs = getMidnightPlusOneMs(calendar);
        }
        return startDayMs;
    } 
    
    public static long getStartOfDayMs(long startTimeMs) {
        // startDate: 12:00:00.001 a.m. on the referenced day
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTimeMs);    
        return getMidnightPlusOneMs(calendar);
    }
    
    /**
     * Returns 11:59:59.001 p.m. on the specified end day.
     * If startDaysMs is null, start day is 01/01/1970.
     * If numberOfDays is null, number of days is Integer.MAX_VALUE.
     * If startDayMs is Tuesday:
     * <pre>
     *  - numberOfDays=1 yields end of day Tuesday
     *  - numberOfDays=2 yields end of day Wednesday
     *  - numberOfDays<1 throws an Exception
     * </pre>
     */
    public static long getEndOfDayMs(Long startDayMs, Integer numberOfDays) 
    throws Exception {
        if(startDayMs == null)
            startDayMs = 1L;
        
        if(numberOfDays == null)
            numberOfDays = Integer.MAX_VALUE;
        
        if(numberOfDays < 1) {
            throw new Exception("numberOfDays must be >= 1; received " + numberOfDays);
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDayMs);   
        
        if(numberOfDays > 1) {
            calendar.add(Calendar.DATE, numberOfDays - 1); 
        }
        
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);      
        return calendar.getTimeInMillis();        
    }
    
    private static long getMidnightPlusOneMs(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 1);            
        return calendar.getTimeInMillis();        
    }
}
