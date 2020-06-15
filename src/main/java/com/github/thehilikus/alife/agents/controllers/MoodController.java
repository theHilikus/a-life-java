package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.animals.moods.Hunting;
import com.github.thehilikus.alife.agents.animals.motions.StraightWalk;
import com.github.thehilikus.alife.agents.animals.motions.TargetWalk;
import com.github.thehilikus.alife.agents.animals.visions.TargetVision;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Locomotion;
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

        Vision vision = new TargetVision(agentId, agent.getGenome(), theWorld, target);
        Locomotion locomotion = new TargetWalk(target, new StraightWalk(agentId, agent.getGenome(), theWorld));
        Hunting result = new Hunting(this, vision, locomotion, target);
        LOG.info("Agent {} transitioning from {} to {}", agentId, currentMood, result);

        return result;
    }
}
