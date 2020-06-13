package com.github.thehilikus.alife.agents;

import com.github.thehilikus.alife.agents.modules.SquareMotion;
import com.github.thehilikus.alife.agents.modules.SquareVision;
import com.github.thehilikus.alife.agents.moods.Scouting;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.IdsSource;
import com.github.thehilikus.alife.world.RandomSource;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A living agent that hunts for food
 */
public class HuntingAgent implements Agent {
    private static final int MAX_SIZE = 50;
    private static final Logger LOG = LoggerFactory.getLogger(HuntingAgent.class.getSimpleName());
    private final Vision vision;
    private final Motion motion;
    private final int size;
    private Mood currentMood;
    private final int id;

    public static List<HuntingAgent> create(int count, World world) {
        List<HuntingAgent> result = new ArrayList<>(count);
        for (int current = 0; current < count; current++) {
            int size = RandomSource.nextInt(MAX_SIZE);

            int id = IdsSource.getNextId();
            Motion motion = SquareMotion.create(id, world);
            Vision vision = SquareVision.create(id, world);

            HuntingAgent newAgent = new HuntingAgent(id, vision, motion, size);
            LOG.info("Created {}", newAgent);
            world.addAgent(newAgent);
            result.add(newAgent);
        }

        return result;
    }

    private HuntingAgent(int id, Vision vision, Motion motion, int size) {
        this.id = id;
        this.vision = vision;
        this.motion = motion;
        this.size = size;

        currentMood = new Scouting(vision, motion);
    }

    @Override
    public void tick() {
        LOG.debug("#### Updating state of {} ####", this);
        Mood newMood = currentMood.tick();
        if (!newMood.getClass().equals(currentMood.getClass())) {
            LOG.debug("{} changing mood from {} to {}", this, currentMood, newMood);
            currentMood = newMood;
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> result = new HashMap<>();
        result.putAll(vision.getParameters());
        result.putAll(motion.getParameters());
        result.putAll(currentMood.getParameters());

        return result;
    }

    @Override
    public Coordinates.Immutable getPosition() {
        return motion.getPosition();
    }

    @Override
    public String toString() {
        return "HuntingAgent{" +
                "id=" + id +
                '}';
    }
}
