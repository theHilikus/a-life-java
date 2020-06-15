package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.modules.SquareVision;
import com.github.thehilikus.alife.agents.modules.TargetVision;
import com.github.thehilikus.alife.agents.modules.TargetWalk;
import com.github.thehilikus.alife.agents.moods.Hunting;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Motion;
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
        int visionRadius = ((SquareVision) agent.getVision()).getRadius();

        Vision vision = new TargetVision(agentId, visionRadius, theWorld, target);
        Motion motion = new TargetWalk(target, null); //TODO: fix
        Hunting result = new Hunting(this, vision, motion, target);
        LOG.info("Agent {} transitioning from {} to {}", agentId, agent.getMood(), result);

        return result;
    }
}
