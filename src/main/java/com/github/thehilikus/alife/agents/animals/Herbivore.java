package com.github.thehilikus.alife.agents.animals;

import com.github.thehilikus.alife.agents.animals.moods.AgentModules;
import com.github.thehilikus.alife.agents.controllers.SocialController;
import com.github.thehilikus.alife.agents.controllers.VitalsController;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A living agent that eats {@link com.github.thehilikus.alife.agents.plants.Plant}
 */
public class Herbivore implements Agent.Social {

    private static final Logger LOG = LoggerFactory.getLogger(Herbivore.class.getSimpleName());
    private final Vision vision;
    private final Locomotion locomotion;
    private final Genome genome;
    private final int id;
    private Mood mood;
    private final SocialController social;
    private final VitalsController vitals;

    Herbivore(int id, AgentModules dependencies, Mood startingMood, VitalsController vitals, SocialController social) {
        this.id = id;
        this.vision = dependencies.getVision();
        this.locomotion = dependencies.getLocomotion();
        this.genome = dependencies.getGenome();
        this.vitals = vitals;
        mood = startingMood;
        this.social = social;
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
    public void communicate(Message message) {
        social.receiveMessage(message);
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
