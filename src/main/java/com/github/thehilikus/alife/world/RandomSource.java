package com.github.thehilikus.alife.world;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A global source of random numbers
 */
public class RandomSource {
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();
    private static final Logger LOG = LoggerFactory.getLogger(RandomSource.class.getSimpleName());

    public static int nextInt(int bound) {
        return nextInt(0, bound);
    }

    public static int get(int fixed) {
        LOG.warn("Hardcoded random");
        return fixed;
    }

    public static double nextDouble(double bound) {
        return random.nextDouble(bound);
    }

    public static int nextInt(int lowerBound, int upperBound) {
        return random.nextInt(lowerBound, upperBound);
    }
}
