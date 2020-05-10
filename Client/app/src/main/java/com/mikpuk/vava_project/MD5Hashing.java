package com.mikpuk.vava_project;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
// TODO comment
//MD5 hashing bol prevzany zo stranky https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
public class MD5Hashing {
        public static String getSecurePassword(String passwordToHash)
        {
            String generatedPassword = null;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(passwordToHash.getBytes());
                byte[] bytes = md.digest();
                StringBuilder sb = new StringBuilder();
                for (byte aByte : bytes) {
                    sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
                }
                generatedPassword = sb.toString();
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            }
            return generatedPassword;
        }

}
