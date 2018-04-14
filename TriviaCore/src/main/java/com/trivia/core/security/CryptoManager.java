package com.trivia.core.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

// Reference implementation is Soteria.
public class CryptoManager {
    private final static int ITERATIONS = 9999; // Aim for half a second.
    private final static int KEY_LENGTH = 160;
    private final static String RANDOM_ALGORITHM = "SHA1PRNG";
    private final static String HASH_ALGORITHM = "PBKDF2WithHmacSHA1";

    public static boolean validateMessage(String providedMessage, String storedHash)
    {
        String[] parts = storedHash.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] storedDigest = Base64.getDecoder().decode((parts[2]));

        PBEKeySpec keySpec = new PBEKeySpec(providedMessage.toCharArray(), salt, iterations, storedDigest.length * 8);

        try {
            byte[] providedDigest = SecretKeyFactory.getInstance(HASH_ALGORITHM).generateSecret(keySpec).getEncoded();
            return slowEquals(providedDigest, storedDigest);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean slowEquals(byte[] hash1, byte[] hash2) {
        int difference = hash1.length ^ hash2.length;
        for (int i = 0; i < hash1.length && i < hash2.length; i++) {
            difference |= hash1[i] ^ hash2[i];
        }
        return difference == 0;
    }

    public static String hashMessage(String message)
    {
        char[] messageChars = message.toCharArray();
        byte[] saltBytes = getSalt();

        PBEKeySpec keySpec = new PBEKeySpec(messageChars, saltBytes, ITERATIONS, KEY_LENGTH);
        try {
            byte[] digest = SecretKeyFactory.getInstance(RANDOM_ALGORITHM).generateSecret(keySpec).getEncoded();
            String storedHash =
                    ITERATIONS
                    + ":"
                    + Base64.getEncoder().encodeToString(saltBytes)
                    + ":"
                    + Base64.getEncoder().encodeToString((digest));
            return storedHash;
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] getSalt()
    {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(RANDOM_ALGORITHM);
            byte[] salt = new byte[KEY_LENGTH / 8];
            secureRandom.nextBytes(salt);
            return salt;
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
