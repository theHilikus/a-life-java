package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.animals.Herbivore;
import com.github.thehilikus.alife.agents.animals.moods.Hunting;
import com.github.thehilikus.alife.agents.animals.moods.Scouting;
import com.github.thehilikus.alife.agents.animals.motions.Legs;
import com.github.thehilikus.alife.agents.animals.motions.StraightWalkWithRandomTurn;
import com.github.thehilikus.alife.agents.animals.visions.SurroundingsVision;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls the transitions between moods
 */
public class HerbivoreMoodController implements MoodController {
    private static final Logger LOG = LoggerFactory.getLogger(HerbivoreMoodController.class.getSimpleName());
    private final World theWorld;
    private final int agentId;

    public HerbivoreMoodController(int agentId, World theWorld) {
        this.agentId = agentId;
        this.theWorld = theWorld;
    }

    @Override
    public Mood startHunting(Agent.Living target) {
        Herbivore agent = (Herbivore) theWorld.getAgent(agentId);

        String currentMood = agent.getDetails().get(Mood.PARAMETER_PREFIX + "current");

        Vision vision = new SurroundingsVision(agentId, agent.getGenome(), theWorld);
        Legs legs = new Legs(agentId, agent.getMovablePosition(), agent.getGenome());
        Hunting result = new Hunting(this, vision, legs, agent.getGenome(), target);
        LOG.info("Agent {} transitioning from {} to {}", agentId, currentMood, result);

        return result;
    }

    @Override
    public Mood startScouting() {
        Herbivore agent = (Herbivore) theWorld.getAgent(agentId);
        Vision vision = new SurroundingsVision(agentId, agent.getGenome(), theWorld);
        Locomotion locomotion = new StraightWalkWithRandomTurn(agentId, agent.getMovablePosition(), agent.getGenome());

        return new Scouting(this, vision, locomotion, agent.getGenome());
    }

    @Override
    public Mood startSleeping() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public Mood startEating() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public Mood startIdling() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }
}
