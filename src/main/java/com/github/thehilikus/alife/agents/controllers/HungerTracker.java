package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Map;

/**
 * Monitors the hunger of an agent
 */
public class HungerTracker implements VitalSign {
    private static final int STARTING_HUNGER = 50;
    /**
     * The point where the agent stops eating
     */
    public static final int FULL_THRESHOLD = 100;

    @Min(0)
    @Max(100)
    private int currentHunger;

    private final int hungryThreshold;

    public HungerTracker(int hungryThreshold) {
        currentHunger = STARTING_HUNGER;
        this.hungryThreshold = hungryThreshold;
    }

    @Override
    public void update(Mood currentMood) {
        int result = currentMood.getHungerDelta();
        currentHunger += result;
    }

    @Override
    public boolean isAlive() {
        return currentHunger > 0;
    }

    @Override
    public int getValue() {
        return currentHunger;
    }

    public Map<String, String> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "hunger", Integer.toString(currentHunger),
                VitalSign.PARAMETER_PREFIX + "hungerRateOfChange", Integer.toString(Mood.HUNGER_DERIVATIVE),
                VitalSign.PARAMETER_PREFIX + "hungryThreshold", Integer.toString(hungryThreshold)
        );
    }

    public boolean isHungry() {
        return currentHunger <= hungryThreshold;
    }

    public boolean isFull() {
        return currentHunger >= FULL_THRESHOLD;
    }

    @Override
    public String toString() {
        return "HungerTracker{" +
                "currentHunger=" + currentHunger +
                '}';
    }
}
