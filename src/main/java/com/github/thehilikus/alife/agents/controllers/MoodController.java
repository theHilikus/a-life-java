package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.animals.visions.SurroundingsVision;
import com.github.thehilikus.alife.agents.animals.visions.TargetVision;
import com.github.thehilikus.alife.agents.animals.motions.TargetWalk;
import com.github.thehilikus.alife.agents.animals.moods.Hunting;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Locomotion;
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
        int visionRadius = ((SurroundingsVision) agent.getVision()).getRadius();

        Vision vision = new TargetVision(agentId, visionRadius, theWorld, target);
        Locomotion locomotion = new TargetWalk(target, null); //TODO: fix
        Hunting result = new Hunting(this, vision, locomotion, target);
        LOG.info("Agent {} transitioning from {} to {}", agentId, agent.getMood(), result);

        return result;
    }
}
