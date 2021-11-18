package com.github.thehilikus.alife.agent.vitals.api;

import com.github.thehilikus.alife.agent.moods.api.Mood;

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
     * @param currentMood the mood the agent is in
     */
    void update(Mood currentMood);

    default void update() {
        update(null);
    }

    boolean isAlive();

    int getValue();

    Map<String, Object> getDetails();
}
