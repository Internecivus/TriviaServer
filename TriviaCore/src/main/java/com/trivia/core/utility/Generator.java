package com.trivia.core.utility;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
public final class Generator {
    private final static String RANDOM_ALGORITHM = "SHA1PRNG"; //TODO: For interoperability reasons use in production only.
    private final static String RANDOM_ALGORITHM_PROVIDER = "SUN";

    private Generator() {}

    // Is not truly random and it is not secure. Use only for non-security, non-critical operations.
    public static int[] generateRandomUniqueArray(int size, int boundsMax) {
        int[] array = new int[size];
        Set<Integer> set = new HashSet<>();

        Random random = new Random();

        while (set.size() < size - 1) {
            int randomNumber = random.nextInt(boundsMax);
            if (set.add(randomNumber)) {
                array[set.size()] = randomNumber;
            }
        }

        return array;
    }

    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static byte[] generateSecureRandomBytes(int size) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[size];
        secureRandom.nextBytes(salt);
        return salt;
    }
}