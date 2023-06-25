package com.github.thehilikus.alife.agent.social;

import com.github.thehilikus.alife.agent.api.AgentDetails;
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
import org.slf4j.LoggerFactory;

/**
 * A living agent that eats {@link com.github.thehilikus.alife.agent.plants.Plant}
 */
public class Herbivore implements SocialAgent {

    private static final Logger LOG = LoggerFactory.getLogger(Herbivore.class);
    private final Vision vision;
    private final Locomotion locomotion;
    private final Genome genome;
    private final int id;
    private Mood mood;
    private final SocialController social;
    private final VitalsController vitals;

    public Herbivore(int id, AgentModules dependencies, Mood startingMood, VitalsController vitals, SocialController social) {
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
    public AgentDetails.Immutable getDetails() {
        AgentDetails details = new AgentDetails(id, getClass().getSimpleName(), getPosition());
        details.addAttribute(Mood.PARAMETER_PREFIX + "current", mood.getClass().getSimpleName());

        details.addAllDetails(vitals.getDetails());
        details.addAllDetails(mood.getDetails());
        details.addAllDetails(vision.getDetails());
        details.addAllDetails(locomotion.getDetails());

        return details.toImmutable();
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
