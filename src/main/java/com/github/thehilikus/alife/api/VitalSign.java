package com.github.thehilikus.alife.api;

/**
 * A metric that affects the agent regardless of mood
 */
public interface VitalSign {
    /**
     * The prefix for vital sign parameters
     */
    String PARAMETER_PREFIX = "vital.";

    /**
     * Updates the vital sign based on the mood
     * @param currentMood the mood the agent is in
     * @return the delta since the last time the vital sign was updated
     */
    int update(Mood currentMood);

    default void update() {
        update(null);
    }

    boolean isAlive();
}
