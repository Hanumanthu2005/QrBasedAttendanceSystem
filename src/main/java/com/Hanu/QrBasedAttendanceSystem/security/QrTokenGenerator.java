package com.Hanu.QrBasedAttendanceSystem.security;

import java.security.SecureRandom;
import java.util.Base64;

public class QrTokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateToken() {
        byte[] randomBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }
}