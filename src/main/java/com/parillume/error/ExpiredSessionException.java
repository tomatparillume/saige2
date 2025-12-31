/*
 * Copyright(c) 2024, Parillume, All rights reserved worldwide
 */
package com.parillume.error;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class ExpiredSessionException extends Exception
                                     implements UnloggedExceptionIF {
    public ExpiredSessionException() {
        super("Session has expired; please log in again");
    }
}
