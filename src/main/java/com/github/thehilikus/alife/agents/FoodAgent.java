package com.github.thehilikus.alife.agents;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.modules.NoMotion;
import com.github.thehilikus.alife.agents.modules.NoVision;
import com.github.thehilikus.alife.agents.moods.NoMood;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.IdsSource;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * An inanimate agent that gets eaten
 */
public class FoodAgent implements Agent {
    private static final Logger LOG = LoggerFactory.getLogger(FoodAgent.class.getSimpleName());
    private final int id;
    private final Vision vision;
    private final Motion motion;
    private final Mood mood;

    public static void create(int count, World world) {
        for (int current = 0; current < count; current++) {
            int id = IdsSource.getNextId();
            Motion motion = NoMotion.create(id, world);
            Vision vision = NoVision.create(id);

            Agent newAgent = new FoodAgent(id, vision, motion);
            LOG.info("Created {}", newAgent);
            world.addAgent(newAgent);
        }
    }

    private FoodAgent(int id, Vision vision, Motion motion) {
        this.id = id;
        this.vision = vision;
        this.motion = motion;

        mood = new NoMood();
    }

    @Override
    public void tick() {
        LOG.debug("#### Updating state of {} ####", this);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Map<String, String> getParameters() {
        return motion.getParameters();
    }

    @Override
    public Coordinates.Immutable getPosition() {
        return motion.getPosition();
    }

    @Override
    public String getStringRepresentation() {
        Ansi.Attribute agentTypeStyle = Ansi.Attribute.LIGHT;

        Ansi.BColor background = Ansi.BColor.GREEN;
        String formatCode = Ansi.generateCode(agentTypeStyle, Ansi.FColor.BLACK, background);
        return Ansi.formatMessage(String.format("%02d", id), formatCode);
    }

    @Override
    public Motion getMotion() {
        return motion;
    }

    @Override
    public Vision getVision() {
        return vision;
    }

    @Override
    public Object getMood() {
        return mood;
    }

    @Override
    public String toString() {
        return "FoodAgent{" +
                "id=" + id +
                '}';
    }
}
