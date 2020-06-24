package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

/**
 * Monitors the hunger of an agent
 */
public class HungerTracker implements VitalSign {
    private static final int STARTING_HUNGER = 50;
    private int currentHunger;

    public HungerTracker() {
        currentHunger = STARTING_HUNGER;
    }

    @Override
    public void update(Mood currentMood) {
        currentHunger += currentMood.getHungerDelta();
    }

    @Override
    public boolean isAlive() {
        return currentHunger > 0;
    }
}
