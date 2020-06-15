package com.github.thehilikus.alife.agents.animals;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.agents.controllers.MoodController;
import com.github.thehilikus.alife.agents.animals.motions.StraightWalkWithRandomTurn;
import com.github.thehilikus.alife.agents.animals.visions.SurroundingsVision;
import com.github.thehilikus.alife.agents.animals.moods.Scouting;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.IdsSource;
import com.github.thehilikus.alife.world.RandomSource;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A living agent that hunts for food
 */
public class Herbivore implements Agent {
    private static final int MAX_SIZE = 50;
    private static final Logger LOG = LoggerFactory.getLogger(Herbivore.class.getSimpleName());
    private final Vision vision;
    private final Locomotion locomotion;
    private final int size;
    private Mood currentMood;
    private final int id;

    public static void create(int count, World world) {
        for (int current = 0; current < count; current++) {
            int size = RandomSource.nextInt(MAX_SIZE);

            int id = IdsSource.getNextId();
            Locomotion locomotion = StraightWalkWithRandomTurn.create(id, world);
            Vision vision = SurroundingsVision.create(id, world);

            Agent newAgent = new Herbivore(id, vision, locomotion, new MoodController(world), size);
            LOG.info("Created {}", newAgent);
            world.addAgent(newAgent);
        }

    }

    private Herbivore(int id, Vision vision, Locomotion locomotion, MoodController moodController, int size) {
        this.id = id;
        this.vision = vision;
        this.locomotion = locomotion;
        this.size = size;

        currentMood = new Scouting(moodController, vision, locomotion, 0.2);
    }

    @Override
    public void tick() {
        LOG.debug("#### Updating state of {} ####", this);
        Mood newMood = currentMood.tick();
        if (!newMood.getClass().equals(currentMood.getClass())) {
            currentMood = newMood;
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("type", getClass().getSimpleName());
        result.putAll(vision.getParameters());
        result.putAll(locomotion.getParameters());
        result.putAll(currentMood.getParameters());

        return result;
    }

    @Override
    public Coordinates.Immutable getPosition() {
        return locomotion.getPosition();
    }

    @Override
    public String getStringRepresentation() {
        Ansi.Attribute agentTypeStyle = Ansi.Attribute.BOLD;

        Ansi.FColor moodColour = currentMood.getTerminalColour();
        Ansi.BColor background = Ansi.BColor.NONE;
        String formatCode = Ansi.generateCode(agentTypeStyle, moodColour, background);
        return Ansi.formatMessage(String.format("%02d", id), formatCode);
    }

    @Override
    public String toString() {
        return "HuntingAgent{" +
                "id=" + id +
                '}';
    }

    @Override
    public Vision getVision() {
        return vision;
    }

    @Override
    public Locomotion getLocomotion() {
        return locomotion;
    }

    @Override
    public Mood getMood() {
        return currentMood;
    }
}
