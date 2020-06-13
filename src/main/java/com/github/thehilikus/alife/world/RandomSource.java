package com.github.thehilikus.alife.world;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * A global source of random numbers
 */
public class RandomSource {
    private static final Random random = new Random();
    private static final Logger LOG = LoggerFactory.getLogger(RandomSource.class.getSimpleName());

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public static int get(int fixed) {
        LOG.warn("Hardcoded random");
        return fixed;
    }

    public static double nextDouble(double bound) {
        return random.nextDouble() * bound;
    }
}
