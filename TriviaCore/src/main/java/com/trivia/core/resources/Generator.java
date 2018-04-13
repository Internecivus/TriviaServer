package com.trivia.core.resources;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by faust. Part of Trivia Project. All rights reserved. 2018
 */
public class Generator {
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
}