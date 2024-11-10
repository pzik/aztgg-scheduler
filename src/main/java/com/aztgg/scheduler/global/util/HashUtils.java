package com.aztgg.scheduler.global.util;

import java.security.MessageDigest;

public class HashUtils {

    public static String encrypt(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes("UTF-8"));
            return bytesToHex(md.digest());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private static String bytesToHex(byte[] bytes) {

        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
