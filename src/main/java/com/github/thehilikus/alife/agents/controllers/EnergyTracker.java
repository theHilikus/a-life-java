package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

/**
 * Monitors the agent's level of energy
 */
public class EnergyTracker implements VitalSign {
    private int currentEnergy;

    public EnergyTracker() {
        currentEnergy = 100;
    }

    public static EnergyTracker create() {
        return new EnergyTracker();
    }

    @Override
    public void update(Mood currentMood) {
        currentEnergy += currentMood.getEnergyDelta();
    }

    @Override
    public boolean isAlive() {
        return currentEnergy > 0;
    }
}
