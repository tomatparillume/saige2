/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.validator.routines.EmailValidator;

/**
 *
 *  * @author tom@parillume.com
 */
public class StringUtil {

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
    
    public static String asHex(byte buf[]) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;

        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }

            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }

        return strbuf.toString();
    }
    
    public static byte[] hexToBytes(String str) {
        char[] hex = str.toCharArray();
        
        int length = hex.length / 2;
        byte[] raw = new byte[length];
        for (int i = 0; i < length; i++) {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << 4) | low;
            if (value > 127) {
                value -= 256;
            }
            raw[i] = (byte) value;
        }
        return raw;
    }
    
    public static String toId(String id) {
        return id.replace("®", "").replace("-","").toLowerCase();
    }
    
    public static String createAlphanumericID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
    public static boolean isEmpty(String s) {
        return (s == null || s.trim().length()==0);
    } 
    
    public static boolean nullEquals(Object a, Object b) {
        if(a == null || b == null) {
            return (a == null && b == null);
        } else {
            return a.equals(b);
        }
    }

    public static boolean nullEquals(String a, String b, boolean caseSensitive) {
        
        boolean nullEquals = nullEquals(a, b);        
        if(nullEquals || caseSensitive) {
            return nullEquals;
        }
               
        if (a == null || b == null) {
            return false;
        } else {
            return a.toUpperCase().equals(b.toUpperCase());
        }
    }    
    
    /**
     * Note that a Collection with an element duplicated will match a list with that
     * element present only once:
     * <pre>
     *      The following lists are considered "equal"
     *      LIST 1:  W X Y Z
     *      LIST 2:  W X Y Z X
     * </pre>
     */
    public static boolean collectionsEqual(Collection coll1, Collection coll2) {
        if(coll1 == null || coll2 == null) {
            return coll1 == null && coll2 == null;
        }     
        return coll1.containsAll(coll2) && coll2.containsAll(coll1);
    }    
    
    public static boolean collectionsIntersect(Collection list1, Collection list2) {
        List list1Copy = new ArrayList(list1);
        list1Copy.retainAll(list2);
        return !list1Copy.isEmpty();
    }
    
    public static List<String> getDuplicateValues(List<String> list) {
        Set<String> duplicateDetector = new HashSet<>();
        return list.stream()
                  .filter(n -> !duplicateDetector.add(n))
                  .collect(Collectors.toList());   
    }
    
    public static int getSharedPrefixCount(String s1, String s2) {
        char[] s1Chars = s1.toCharArray();
        char[] s2Chars = s2.toCharArray();
        
        int len = s1Chars.length < s2Chars.length ?
                  s1Chars.length :
                  s2Chars.length;
        
        int sharedPrefixCount = 0;
        for(int i=0; i < len; i++) {
            if(s1Chars[i] == s2Chars[i])
                sharedPrefixCount++;
            else
                break;
        }
        
        return sharedPrefixCount;
    }    
}
