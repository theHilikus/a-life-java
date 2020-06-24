package com.github.thehilikus.alife.api;

/**
 * A metric that affects the agent regardless of mood
 */
public interface VitalSign {
    /**
     * The prefix for vital sign parameters
     */
    String PARAMETER_PREFIX = "vital.";

    void update(Mood currentMood);

    default void update() {
        update(null);
    }

    boolean isAlive();
}
