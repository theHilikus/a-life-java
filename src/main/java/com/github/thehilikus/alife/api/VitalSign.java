package com.github.thehilikus.alife.api;

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
     * Updates the vital sign based on the mood
     * @param currentMood the mood the agent is in
     */
    void update(Mood currentMood);

    default void update() {
        update(null);
    }

    boolean isAlive();

    int getValue();

    Map<String, Object> getParameters();
}
