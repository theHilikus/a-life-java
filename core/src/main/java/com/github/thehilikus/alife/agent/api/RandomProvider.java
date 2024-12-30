package com.github.thehilikus.alife.agent.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * A global source of random numbers
 */
public class RandomProvider {
    private static final Random random = new Random();
    private static final Logger LOG = LoggerFactory.getLogger(RandomProvider.class);

    /**
     * Set the seed for the random number generator. This is useful for reproducible simulations.
     *
     * @param seed the seed for the random number generator
     */
    public static void setSeed(long seed) {
        LOG.info("Starting simulation with RNG seed = {}", seed);
        random.setSeed(seed);
    }

    /**
     * @param upperBound the upper bound of the range (exclusive)
     * @return a random number from 0 to {@code upperBound - 1}
     */
    public static int nextInt(int upperBound) {
        return nextInt(0, upperBound);
    }

    /**
     * Returns a fixed integer value. This method is intended for situations where a predetermined "random" value is required, despite the randomness
     * implied by the class name.
     *
     * @param fixed the predetermined integer value to return
     * @return the fixed integer value provided as input
     */
    public static int getFixedInteger(int fixed) {
        LOG.warn("Hardcoded random");
        return fixed;
    }

    /**
     * Returns a random double value from 0.0 (inclusive) to {@code upperBound} (exclusive). The returned value is a random double
     * value in the range of 0.0 to 1.0 multiplied by the bound.
     *
     * @param upperBound the upper bound of the range (exclusive)
     * @return a random double value from 0.0 to {@code upperBound}
     */
    public static double nextDouble(double upperBound) {
        return nextDouble(0, upperBound);
    }

    /**
     * Returns a random double value from {@code lowerBound} to {@code upperBound} (exclusive).
     * The returned value is {@code lowerBound} plus a random double value in the range of 0.0 to 1.0 multiplied by the range of the two bounds.
     *
     * @param lowerBound the lower bound of the range (inclusive)
     * @param upperBound the upper bound of the range (exclusive)
     * @return a random double value from {@code lowerBound} to {@code upperBound}
     */
    public static double nextDouble(double lowerBound, double upperBound) {
        return lowerBound + random.nextDouble() * (upperBound - lowerBound);
    }

    /**
     * @param lowerBound the lower bound of the range (inclusive)
     * @param upperBound the upper bound of the range (exclusive)
     * @return a random number from {@code lowerBound} to {@code upperBound - 1}
     */
    public static int nextInt(int lowerBound, int upperBound) {
        return lowerBound + random.nextInt(upperBound - lowerBound);
    }

    /**
     * @return a random boolean value
     */
    public static boolean nextBoolean() {
        return random.nextInt(2) == 0;
    }
}
