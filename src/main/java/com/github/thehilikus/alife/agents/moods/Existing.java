package com.github.thehilikus.alife.agents.moods;

import com.github.thehilikus.alife.api.Mood;

import java.util.Map;

/**
 * The basic behaviour of an agent, regardless of mood
 */
public class Existing implements Mood {
    private static final int HUNGER_DERIVATIVE = -2;
    private static final int ENERGY_DERIVATIVE = -2;
    private final int agentId;

    public Existing(int agentId) {
        this.agentId = agentId;
    }

    @Override
    public Mood tick() {
        return null;
    }

    @Override
    public int getHungerDelta() {
        return HUNGER_DERIVATIVE;
    }

    @Override
    public int getEnergyDelta() {
        return ENERGY_DERIVATIVE;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }
}
