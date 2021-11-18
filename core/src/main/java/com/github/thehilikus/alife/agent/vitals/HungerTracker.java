package com.github.thehilikus.alife.agent.vitals;

import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Map;

/**
 * Monitors the hunger of an agent
 */
public class HungerTracker implements VitalSign {
    private static final int STARTING_HUNGER = 50;

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
        currentHunger += currentMood.getHungerDelta();
    }

    @Override
    public boolean isAlive() {
        return currentHunger > 0;
    }

    @Override
    public int getValue() {
        return currentHunger;
    }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of(
                PARAMETER_PREFIX + "hunger", currentHunger,
                PARAMETER_PREFIX + "hungryThreshold", hungryThreshold
        );
    }

    public boolean isHungry() {
        return currentHunger <= hungryThreshold;
    }

    public boolean isFullAfter(int lastBite) {
        return currentHunger + lastBite >= FULL_THRESHOLD;
    }

    @Override
    public String toString() {
        return "HungerTracker{" +
                "currentHunger=" + currentHunger +
                '}';
    }
}
