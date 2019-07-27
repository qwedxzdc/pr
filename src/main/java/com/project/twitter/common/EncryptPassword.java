package com.project.twitter.common;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptPassword {
    private static final int radix = 16;
    private static final int hashLengthLimit = 32;

    public static String encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] messageDigestBytes = messageDigest.digest(password.getBytes());
        BigInteger intHash = new BigInteger(1, messageDigestBytes);
        String hash = intHash.toString(radix);
        while (hash.length() < hashLengthLimit) {
            hash = "0" + hash;
        }
        return hash;
    }
}