package com.github.thehilikus.alife.world;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * A global source of random numbers
 */
public class RandomProvider {
    private static final Random random = new Random();
    private static final Logger LOG = LoggerFactory.getLogger(RandomProvider.class.getSimpleName());
    private static final int FIXED_SEED = 311;

    public static void fixSeed() {
        random.setSeed(FIXED_SEED);
    }

    public static int nextInt(int bound) {
        return nextInt(0, bound);
    }

    public static int get(int fixed) {
        LOG.warn("Hardcoded random");
        return fixed;
    }

    public static double nextDouble(double bound) {
        return random.nextDouble() * bound;
    }

    public static int nextInt(int lowerBound, int upperBound) {
        return random.nextInt(upperBound - lowerBound) + lowerBound;
    }
}
