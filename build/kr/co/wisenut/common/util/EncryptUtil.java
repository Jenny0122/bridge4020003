/*
 * @(#)EncryptUtil.java   3.8.1 2009/03/11
 *
 */
package kr.co.wisenut.common.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * EncryptUtil
 *
 * Copyright 2001-2009 KoreaWISEnut, Inc. All Rights Reserved.
 * This software is the proprietary information of WISEnut, Inc.
 * Bridge Release 11 March 2009
 *
 * @author  WISEnut
 * @version 3.8,1. 2009/03/11 Bridge Release
 *
 */
public class EncryptUtil {
    static final int key = 0xF3; // encrypt/decrypt key
    static final String aeskey = "KingOfWisenutDev";

    public static String MD5(String str) {
        return MD5(str.getBytes());
    }

    /**
     * MD5  encrypt method
     * @param msg original data
     * @return encrypt data
     */
    public static String MD5(byte[] msg) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(msg);

            byte[] digest = md.digest();
            StringBuffer buf = new StringBuffer(digest.length * 2);

            for (int i = 0; i < digest.length; i++) {
                int intVal = digest[i] & 0xff;

                if (intVal < 0x10) {
                    buf.append("0");
                }

                buf.append(Integer.toHexString(intVal));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException ne) {
            return null;
        }
    }

    /**
     * SHA-1 encrypt method
     * @param data original data
     * @return encrypt data
     */
    public static String SHA1(byte[] data) {
        MessageDigest md ;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        md.update(data);
        byte[] encdata = md.digest();

        StringBuffer sb = new StringBuffer();
        for(int i=0; i<encdata.length; i++){
            sb.append(Integer.toHexString( encdata[i] & 0xff ));
        }

        return sb.toString();
    }

    public static String encryptString(String str) {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i < str.length(); i++){
            int c = str.charAt(i);
            sb.append(Integer.toHexString(c ^ key)).append(" ");
        }
        return sb.toString();
    }

    public static String decryptString(String str){
        String[] arry = StringUtil.split(str, " ");
        StringBuffer sb = new StringBuffer();
        for(int n=0; n < arry.length; n++){
            String temp = arry[n];
            char ch = (char)(Integer.parseInt(temp, 16) ^ key);
            sb.append(ch);
        }
        return sb.toString();
    }
    
    public static String encryptStringAES(String str)
    {
    	Key secureKey = new SecretKeySpec(aeskey.getBytes(), "AES");
    	
    	try {
	    	Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secureKey);
			
			// byte[] to hex
			byte[] encrypt = cipher.doFinal(str.getBytes());
			StringBuffer sb = new StringBuffer(encrypt.length * 2);
			
			for(int i = 0; i < encrypt.length; i++)
			{
				String hex = "0" + Integer.toHexString(0xff & encrypt[i]);
				sb.append(hex.substring(hex.length() - 2));
			}
			
			return sb.toString();
			
    	} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
    	
    	return "";
    }
    
    public static String decryptStringAES(String str) {
    	String hex = str;
        byte[] byteArray = new byte[hex.length()/2];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        
        Key secureKey = new SecretKeySpec(aeskey.getBytes(), "AES");
        
        try {
			Cipher cipher = Cipher.getInstance("AES");
			
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secureKey);
			byte[] plainText = cipher.doFinal(byteArray);
			
			return new String(plainText);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
        
        return "";
    }

    public static boolean isHexa(String str){
        boolean isHexa = false;
        if (str.startsWith("0x00")) {
             isHexa = true;
        }

        return isHexa;
    }
    
    public static void main(String[] args) {
		String test = "bslim511";
		String encrypt = EncryptUtil.encryptStringAES(test);
		System.out.println(encrypt);
		String decrypt = EncryptUtil.decryptStringAES(encrypt);
		System.out.println(decrypt);
	}
}
