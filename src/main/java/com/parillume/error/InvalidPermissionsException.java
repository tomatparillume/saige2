/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.error;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class InvalidPermissionsException extends Exception 
                                         implements UnloggedExceptionIF {
    public InvalidPermissionsException(String message) {
        super(message);
    }
}
