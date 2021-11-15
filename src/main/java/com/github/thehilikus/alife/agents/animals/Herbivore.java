package com.github.thehilikus.alife.agents.animals;

import com.github.thehilikus.alife.agents.animals.moods.Existing;
import com.github.thehilikus.alife.agents.animals.motions.StraightWalkWithRandomTurn;
import com.github.thehilikus.alife.agents.animals.visions.SurroundingsVision;
import com.github.thehilikus.alife.agents.controllers.*;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.agents.genetics.HerbivoreGenome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.IdsProvider;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A living agent that eats {@link com.github.thehilikus.alife.agents.plants.Plant}
 */
public class Herbivore implements Agent.Movable, Agent.Evolvable {

    private static final Logger LOG = LoggerFactory.getLogger(Herbivore.class.getSimpleName());
    private final Vision vision;
    private final Locomotion locomotion;

    private final Genome genome;

    private final int id;
    private Mood mood;
    private final VitalsController vitals;

    Herbivore(int id, Genome genome, Vision vision, Locomotion locomotion, Mood startingMood, VitalsController vitals) {
        this.id = id;
        this.vision = dependencies.getVision();
        this.locomotion = dependencies.getLocomotion();
        this.genome = dependencies.getGenome();
        this.vitals = vitals;
        mood = startingMood;
    }

    @Override
    public VitalSign tick() {
        LOG.debug("#### Updating state of {} ####", this);
        Mood oldMood = mood;
        mood = oldMood.tick();
        Mood priorityMood = vitals.update(oldMood, mood);
        boolean alive = vitals.isAlive();
        if (alive && !priorityMood.getClass().equals(mood.getClass())) {
            LOG.info("Overwriting mood {} with {} due to vital need", mood, priorityMood);
            mood = priorityMood;
        } else if (oldMood != mood) {
            LOG.info("Transitioning from {} to {}", oldMood, mood);
        }

        return alive ? null : vitals.getCauseOfDeath();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Map<String, Object> getDetails() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("type", getClass().getSimpleName());
        result.put("position", getPosition());
        result.put(Mood.PARAMETER_PREFIX + "current", mood.getClass().getSimpleName());

        result.putAll(vitals.getParameters());
        result.putAll(mood.getParameters());
        result.putAll(vision.getParameters());
        result.putAll(locomotion.getParameters());
        result.putAll(genome.getParameters());

        return result;
    }

    @Override
    public String toString() {
        return "Herbivore{" +
                "id=" + id +
                ", position=" + getPosition() +
                ", mood=" + mood +
                '}';
    }

    @Override
    public Genome getGenome() {
        return genome;
    }

    @Override
    public Position.Immutable getPosition() {
        return locomotion.getPosition();
    }

    @Override
    public int getOrientation() {
        return locomotion.getOrientation();
    }
}
