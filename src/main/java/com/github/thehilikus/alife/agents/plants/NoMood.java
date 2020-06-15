package com.github.thehilikus.alife.agents.plants;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.Mood;

import java.util.Map;

/**
 * Do nothing
 */
public class NoMood implements Mood {
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

    @Override
    public Ansi.FColor getTerminalColour() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public int getAgentId() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public Map<String, String> getParameters() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }
}
