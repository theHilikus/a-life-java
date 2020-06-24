package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.animals.moods.Hunting;
import com.github.thehilikus.alife.agents.animals.motions.Legs;
import com.github.thehilikus.alife.agents.animals.visions.SurroundingsVision;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.Vision;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls the transitions between moods
 */
public class MoodController {
    private static final Logger LOG = LoggerFactory.getLogger(MoodController.class.getSimpleName());
    private final World theWorld;

    public MoodController(World theWorld) {
        this.theWorld = theWorld;
    }

    public Hunting startHunting(int agentId, Agent target) {
        Agent agent = theWorld.getAgent(agentId);

        String currentMood = agent.getDetails().get(Mood.PARAMETER_PREFIX + "current");

        Vision vision = new SurroundingsVision(agentId, agent.getGenome(), theWorld);
        Legs legs = new Legs(agentId, agent.getMovablePosition(), agent.getGenome());
        Hunting result = new Hunting(this, vision, legs, agent.getGenome(), target);
        LOG.info("Agent {} transitioning from {} to {}", agentId, currentMood, result);

        return result;
    }

    public Mood startScouting() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    public Mood startSleeping() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    public Mood startEating() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }
}
