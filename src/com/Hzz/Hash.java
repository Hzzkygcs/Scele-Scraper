package com.Hzz;

import static java.lang.System.out;
import java.math.BigInteger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public static String sha512(String string_to_hash){
        byte[] input_bytes = string_to_hash.getBytes();

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("sha-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Not found sha-512 algorithm");
        }
        byte[] result = messageDigest.digest(input_bytes);

        return bytesToHex(result);

    }

    public static String bytesToHex(byte[] bytes){
        return bytesToRadix(bytes, 16);
    }

    public static String bytesToRadix(byte[] bytes, int radix){
        // 1 means positive. bytes will be converted into positive integer
        BigInteger int_repr = new BigInteger(1, bytes);
        return int_repr.toString(radix);
    }




}
