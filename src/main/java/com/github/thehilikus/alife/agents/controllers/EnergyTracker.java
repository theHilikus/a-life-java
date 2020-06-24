package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Component;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

import java.util.Map;

/**
 * Monitors the agent's level of energy
 */
public class EnergyTracker implements VitalSign, Component {
    private final int agentId;
    private int currentEnergy;
    private final int lowEnergyThreshold;

    public EnergyTracker(int agentId, Genome genome) {
        this.agentId = agentId;
        currentEnergy = 100;
        lowEnergyThreshold = genome.getGene(PARAMETER_PREFIX + "lowEnergyThreshold");
    }

    @Override
    public void update(Mood currentMood) {
        currentEnergy += currentMood.getEnergyDelta();
    }

    @Override
    public boolean isAlive() {
        return currentEnergy > 0;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(PARAMETER_PREFIX + "energy", Integer.toString(currentEnergy));
    }

    public boolean isTired() {
        return currentEnergy <= lowEnergyThreshold;
    }
}
