package com.github.thehilikus.alife.agent.vitals.api;

import java.util.Map;

/**
 * A metric that affects the agent regardless of mood
 */
public interface VitalSign {
    /**
     * The prefix for vital sign parameters
     */
    String PARAMETER_PREFIX = "vitals.";
    /**
     * the maximum possible level of energy
     */
    int MAX_ENERGY = 100;
    /**
     * The point where the agent stops eating
     */
    int FULL_THRESHOLD = 100;

    /**
     * Updates the vital sign based on the mood
     *
     * @param delta the change in the vital sign
     */
    void update(int delta);

    default void update() {
        update(0);
    }

    boolean isAlive();

    int getValue();

    Map<String, Object> getDetails();
}
