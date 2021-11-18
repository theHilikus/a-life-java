package com.github.thehilikus.alife.agent.social;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.vitals.AgentModules;
import com.github.thehilikus.alife.agent.api.Message;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.controllers.SocialController;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
import com.github.thehilikus.alife.agent.controllers.VitalsController;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.vision.api.Vision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A living agent that eats {@link com.github.thehilikus.alife.agent.plants.Plant}
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
        Mood newMood = mood.tick(this);
        vitals.updateTrackers(mood);

        Mood vitalMood = vitals.nextMood(newMood);
        if (vitalMood.getPriority() > newMood.getPriority()) {
            LOG.info("Overwriting next mood {} with {} due to vital needs", newMood, vitalMood);
            newMood = vitalMood;
        }
        boolean alive = vitals.isAlive();

        Mood socialMood = social.nextMood(newMood);
        if (socialMood.getPriority() > newMood.getPriority()) {
            LOG.info("Overwriting next mood {} with {} due to social interaction", newMood, socialMood);
            newMood = socialMood;
        }

        if (alive && newMood != mood) {
            LOG.info("Transitioning from {} to {}", mood, newMood);
        }
        mood = newMood;

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

        result.putAll(vitals.getDetails());
        result.putAll(mood.getDetails());
        result.putAll(vision.getDetails());
        result.putAll(locomotion.getDetails());

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
