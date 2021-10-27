package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Map;

/**
 * Monitors the agent's level of energy
 */
public class EnergyTracker implements VitalSign {
    /**
     * The number of energy points to lose per round
     */
    public static final int ENERGY_DERIVATIVE = -2;
    private static final Logger LOG = LoggerFactory.getLogger(EnergyTracker.class.getSimpleName());
    private static final int STARTING_ENERGY = 100;
    /**
     * the maximum possible level of energy
     */
    public static final int MAX_ENERGY = 100;
    private final int agentId;

    @Min(0)
    @Max(MAX_ENERGY)
    private int currentEnergy;

    private final int lowEnergyThreshold;

    public EnergyTracker(int agentId) {
        this(agentId, 0);
    }

    public EnergyTracker(int agentId, int lowEnergyThreshold) {
        this.agentId = agentId;
        currentEnergy = STARTING_ENERGY;
        this.lowEnergyThreshold = lowEnergyThreshold;
    }

    @Override
    public void update(Mood currentMood) {
        currentEnergy += currentMood.getEnergyDelta();
        currentEnergy = Math.max(0, currentEnergy);
        currentEnergy = Math.min(MAX_ENERGY, currentEnergy);
    }

    @Override
    public boolean isAlive() {
        return currentEnergy > 0;
    }

    @Override
    public int getValue() {
        return currentEnergy;
    }

    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, Object> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "energy", currentEnergy,
                PARAMETER_PREFIX + "energyRateOfChange", EnergyTracker.ENERGY_DERIVATIVE,
                PARAMETER_PREFIX + "lowEnergyThreshold", lowEnergyThreshold
        );
    }

    public boolean isTired() {
        return currentEnergy <= lowEnergyThreshold;
    }

    public boolean isRested() {
        boolean rested = currentEnergy >= MAX_ENERGY;
        if (!rested) {
            LOG.debug("Agent {} energy: {}", agentId, currentEnergy);
        }

        return rested;
    }

    @Override
    public String toString() {
        return "EnergyTracker{" +
                "currentEnergy=" + currentEnergy +
                '}';
    }
}
