package com.github.thehilikus.alife.agents.moods;

import com.github.thehilikus.alife.agents.FoodAgent;
import com.github.thehilikus.alife.api.Mood;

import java.util.Map;

/**
 * When food has been located
 */
public class Hunting implements Mood {
    public Hunting(FoodAgent foodAgent) {
    }

    @Override
    public int getAgentId() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public Map<String, String> getParameters() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public Mood tick() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public int getHungerDelta() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public int getEnergyDelta() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }
}
