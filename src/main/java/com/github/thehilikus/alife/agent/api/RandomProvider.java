package com.github.thehilikus.alife.agent.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * A global source of random numbers
 */
public class RandomProvider {
    private static final Random random = new Random();
    private static final Logger LOG = LoggerFactory.getLogger(RandomProvider.class.getSimpleName());

    public static void setSeed(long seed) {
        LOG.info("Starting simulation with RNG seed = {}", seed);
        random.setSeed(seed);
    }

    public static int nextInt(int upperBound) {
        return nextInt(0, upperBound);
    }

    public static int get(int fixed) {
        LOG.warn("Hardcoded random");
        return fixed;
    }

    public static double nextDouble(double lowerBound, double upperBound) {
        return lowerBound + random.nextDouble() * (upperBound - lowerBound);
    }

    public static double nextDouble(double bound) {
        return nextDouble(0, bound);
    }

    public static int nextInt(int lowerBound, int upperBound) {
        return lowerBound + random.nextInt(upperBound - lowerBound);
    }

    public static boolean nextBoolean() {
        return random.nextInt(2) == 0;
    }
}
