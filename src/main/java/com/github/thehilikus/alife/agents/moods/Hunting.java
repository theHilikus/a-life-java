package com.github.thehilikus.alife.agents.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.MoodController;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.Motion;
import com.github.thehilikus.alife.api.Vision;

import java.util.Map;

/**
 * When food has been located
 */
public class Hunting implements Mood {
    public Hunting(MoodController moodController, Vision vision, Motion motion, Agent foodAgent) {

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

    @Override
    public Ansi.FColor getTerminalColour() {
        return Ansi.FColor.YELLOW;
    }
}
