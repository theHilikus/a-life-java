package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Component;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Map;

/**
 * Monitors the hunger of an agent
 */
public class HungerTracker implements VitalSign, Component {
    private static final int STARTING_HUNGER = 50;
    private final int agentId;

    @Min(0)
    @Max(100)
    private int currentHunger;

    private final int hungryThreshold;

    public HungerTracker(int agentId, Genome genome) {
        this.agentId = agentId;
        currentHunger = STARTING_HUNGER;
        hungryThreshold = genome.getGene(PARAMETER_PREFIX + "hungryThreshold");
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
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(PARAMETER_PREFIX + "hunger", Integer.toString(currentHunger));
    }

    public boolean isHungry() {
        return currentHunger <= hungryThreshold;
    }
}
