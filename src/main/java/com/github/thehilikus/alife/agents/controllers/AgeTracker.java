package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

/**
 * Monitors the age of an agent
 */
public class AgeTracker implements VitalSign {
    private final int lifeExpectancy;
    private int currentAge;

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
}
