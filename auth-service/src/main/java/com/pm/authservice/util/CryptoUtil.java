package com.pm.authservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;

import java.security.SecureRandom;

public class CryptoUtil {
    private static Logger log = LoggerFactory.getLogger(CryptoUtil.class);

    public enum MessageDigestAlgorithm {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256");
        private final String algorithm;

        private MessageDigestAlgorithm(final String algorithm) {
            this.algorithm = algorithm;
        }

        public String getAlgorithm() {
            return this.algorithm;
        }
    }

    public static String generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = secureRandom.generateSeed(32);
        return new String(Hex.encode(salt));
    }
}
