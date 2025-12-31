/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util;

/**
 *
 * @author tmargolis
 * @author tom@parillume.com
 */
public class CryptionUtil {
    private static final String GLOBAL_ENCRYPTION_KEY = "http:5662dTh44(*#@_)!fh44P(6f0pp9^smtp:8933pgg[]";
    
    /**
     * If the submitted String is encrypted, decrypt and return it; otherwise,
     * return the submitted String.
     */
    public static String conditionallyDecryptGlobal(String data) {   
        String ret = data;
        try {
            ret = decryptGlobal(data);
        } catch(Exception exc) {
            // data is not encrypted
        }
        return ret;
    }
    
    public static String encryptGlobal(String data) {
        if (StringUtil.isEmpty(data)) {
            return "";
        }
        
        DesEncrypter encrypter = new DesEncrypter(GLOBAL_ENCRYPTION_KEY);
        String _enc = encrypter.encrypt(data);
        
        return (!StringUtil.isEmpty(_enc)) ? _enc : "";
    }

    public static String decryptGlobal(String data) throws Exception {
        if (StringUtil.isEmpty(data)) {
            return "";
        }
        
        DesEncrypter encrypter = new DesEncrypter(GLOBAL_ENCRYPTION_KEY);
        String _dec = encrypter.decrypt(data);
        if(StringUtil.isEmpty(_dec))
            throw new Exception(data + " is not encrypted and cannot be decrypted");
        
        return _dec;
    }

    // return DES encrypted string
    public static String encrypt(String key, String data) {
        DesEncrypter encrypter = new DesEncrypter(key);
        String _enc = encrypter.encrypt(data);
        
        return (!StringUtil.isEmpty(_enc)) ? _enc : "";
    }

    // return DES decrypted string
    public static String decrypt(String key, String data) {
        DesEncrypter encrypter = new DesEncrypter(key);
        String _dec = encrypter.decrypt(data);

        return (!StringUtil.isEmpty(_dec)) ? _dec : "";
    }   
}
