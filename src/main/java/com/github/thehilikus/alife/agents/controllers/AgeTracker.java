package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

/**
 * Monitors the age of an agent
 */
public class AgeTracker implements VitalSign {
    @PositiveOrZero
    private int currentAge;
    private final int teenAge;
    private final int lifeExpectancy;

    public AgeTracker(int teenAge, int lifeExpectancy) {
        this.teenAge = teenAge;
        this.lifeExpectancy = lifeExpectancy;
    }

    @Override
    public void update(Mood currentMood) {
        currentAge++;
    }

    @Override
    public boolean isAlive() {
        return currentAge <= lifeExpectancy;
    }

    @Override
    public int getValue() {
        return currentAge;
    }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of(
                PARAMETER_PREFIX + "age", currentAge,
                PARAMETER_PREFIX + "teenAge", teenAge,
                PARAMETER_PREFIX + "lifeExpectancy", lifeExpectancy
        );
    }

    @Override
    public String toString() {
        return "AgeTracker{" +
                "currentAge=" + currentAge +
                ", lifeExpectancy=" + lifeExpectancy +
                '}';
    }

    public boolean isTeenAge() {
        return currentAge >= teenAge;
    }
}
