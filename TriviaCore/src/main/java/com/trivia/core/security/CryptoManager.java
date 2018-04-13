package com.trivia.core.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */
public class CryptoManager {
    private final static int ITERATIONS = 9999; // Aim for half a second.
    private final static int KEY_LENGTH = 160;
    private final static String RANDOM_ALGORITHM = "SHA1PRNG";
    private final static String HASH_ALGORITHM = "PBKDF2WithHmacSHA1";

    public static boolean validateMessage(String providedMessage, String storedHash) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        String[] parts = storedHash.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] storedDigest = fromHex(parts[2]);

        PBEKeySpec keySpec = new PBEKeySpec(providedMessage.toCharArray(), salt, iterations, storedDigest.length * 8);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
        byte[] providedDigest = secretKeyFactory.generateSecret(keySpec).getEncoded();

        return slowEquals(providedDigest, storedDigest);
    }

    private static boolean slowEquals(byte[] hash1, byte[] hash2) {
        int difference = hash1.length ^ hash2.length;
        for (int i = 0; i < hash1.length && i < hash2.length; i++) {
            difference |= hash1[i] ^ hash2[i];
        }
        return difference == 0;
    }

    public static String hashMessage(String message) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        char[] messageChars = message.toCharArray();
        byte[] saltBytes = getSalt();

        PBEKeySpec keySpec = new PBEKeySpec(messageChars, saltBytes, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(RANDOM_ALGORITHM);
        byte[] digest = secretKeyFactory.generateSecret(keySpec).getEncoded();

        return ITERATIONS + ":" + toHex(saltBytes) + ":" + toHex(digest);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom secureRandom = SecureRandom.getInstance(RANDOM_ALGORITHM);
        byte[] salt = new byte[KEY_LENGTH / 8];
        secureRandom.nextBytes(salt);
        return salt;
    }

    //TODO: toHex and fromHex needs to be audited.
    private static String toHex(byte[] array)
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }
        else {
            return hex;
        }
    }

    private static byte[] fromHex(String hex)
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length; i++) {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
