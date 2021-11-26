package com.github.thehilikus.alife.agent.social;

import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.api.internal.Message;
import com.github.thehilikus.alife.agent.api.internal.SocialAgent;
import com.github.thehilikus.alife.agent.controllers.SocialController;
import com.github.thehilikus.alife.agent.controllers.VitalsController;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.vision.api.Vision;
import com.github.thehilikus.alife.agent.vitals.AgentModules;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BasicSocialAgent implements SocialAgent {
    private final Vision vision;
    private final Locomotion locomotion;
    private final Genome genome;
    private final int id;
    private Mood mood;
    private final SocialController social;
    private final VitalsController vitals;

    public BasicSocialAgent(int id, AgentModules dependencies, Mood startingMood, VitalsController vitals, SocialController social) {
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
        getLog().debug("#### Updating state of {} ####", this);
        Mood newMood = mood.tick(this);
        vitals.updateTrackers(mood);

        Mood vitalMood = vitals.nextMood(newMood);
        if (vitalMood.getPriority() > newMood.getPriority()) {
            getLog().info("Overwriting next mood {} with {} due to vital needs", newMood, vitalMood);
            newMood = vitalMood;
        }
        boolean alive = vitals.isAlive();

        Mood socialMood = social.nextMood(newMood);
        if (socialMood.getPriority() > newMood.getPriority()) {
            getLog().info("Overwriting next mood {} with {} due to social interaction", newMood, socialMood);
            newMood = socialMood;
        }

        if (alive && newMood != mood) {
            getLog().info("Transitioning from {} to {}", mood, newMood);
        }
        mood = newMood;

        return alive ? null : vitals.getCauseOfDeath();
    }

    protected abstract Logger getLog();

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

    @Override
    public Mood getMood() {
        return mood;
    }
}
