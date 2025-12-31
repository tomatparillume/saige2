/*
 * Copyright(c) 2024 Parillume, All rights reserved worldwide
 */
package com.parillume.util;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.spec.*;
import java.io.*;

/**
 * @author tmargolis
 * @author tom@parillume.com
 */
public class DesEncrypter {
        Cipher ecipher;
        Cipher dcipher;
    
        // 8-byte Salt
        byte[] salt = {
            (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
            (byte)0x86, (byte)0x35, (byte)0xE3, (byte)0x03
        };
    
        // Iteration count
        int iterationCount = 9;
    
        public DesEncrypter(String passPhrase) {
            try {
                // Create the key
                PBEKeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
                SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
                ecipher = Cipher.getInstance(key.getAlgorithm());
                dcipher = Cipher.getInstance(key.getAlgorithm());
    
                // Prepare the parameter to the ciphers
                AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
    
                // Create the ciphers
                ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
                dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
            } catch (java.security.InvalidAlgorithmParameterException e) {
                System.out.println("DesEncrypter: "+e.getMessage());
            } catch (java.security.spec.InvalidKeySpecException e) {
                System.out.println("DesEncrypter: "+e.getMessage());
            } catch (javax.crypto.NoSuchPaddingException e) {
                System.out.println("DesEncrypter: "+e.getMessage());
            } catch (java.security.NoSuchAlgorithmException e) {
                System.out.println("DesEncrypter: "+e.getMessage());
            } catch (java.security.InvalidKeyException e) {
                System.out.println("DesEncrypter: "+e.getMessage());
            }
        }
    
        public String encrypt(String str) {
            try {
                // Encode the string into bytes using utf-8
                byte[] utf8 = str.getBytes("UTF8");
    
                // Encrypt
                byte[] enc = ecipher.doFinal(utf8);
    
                // Encode bytes to base64 to get a string
                //return new sun.misc.BASE64Encoder().encode(enc);
                return StringUtil.asHex(enc);
            } catch (javax.crypto.BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
                System.out.println("DesEncrypter::encrypt: "+e.getMessage());
            }
            return null;
        }
    
        public String decrypt(String str) {
            try {
                // Decode base64 to get bytes
                //byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
                byte[] dec = StringUtil.hexToBytes(str);
    
                // Decrypt
                byte[] utf8 = dcipher.doFinal(dec);
    
                // Decode using utf-8
                return new String(utf8, "UTF8");
            } catch (javax.crypto.BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
                System.out.println("DesEncrypter::decrypt: "+e.getMessage());
            }
            return null;
        }
        
/*    
	public static void main(String[] args)
	{
            try {
                // Create encrypter/decrypter class
                DesEncrypter encrypter = new DesEncrypter("Your password");

                // Encrypt
                String encrypted = encrypter.encrypt("String you want to encrypt");
                // Decrypt
                String decrypted = encrypter.decrypt(encrypted);
            } catch (Exception e) {
            }
	}
 */
    }