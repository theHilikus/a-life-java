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
    private final int lifeExpectancy;

    public AgeTracker(int lifeExpectancy) {
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

    public Map<String, String> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "age", Integer.toString(currentAge),
                PARAMETER_PREFIX + "lifeExpectancy", Integer.toString(lifeExpectancy)
        );
    }

    @Override
    public String toString() {
        return "AgeTracker{" +
                "currentAge=" + currentAge +
                ", lifeExpectancy=" + lifeExpectancy +
                '}';
    }
}
