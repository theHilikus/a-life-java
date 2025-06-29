package com.github.thehilikus.alife.agent.plants;

import com.github.thehilikus.alife.agent.api.AgentDetails;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.api.internal.EatableAgent;
import com.github.thehilikus.alife.agent.moods.BeingEaten;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.vitals.SizeTracker;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An inanimate agent that gets eaten
 */
public class Plant implements EatableAgent {
    private static final Logger LOG = LoggerFactory.getLogger(Plant.class);
    private final int id;
    private final double pollinationProbability;
    private final int pollinationPeriod;
    private final Position position;
    private final int maxSize;
    private Mood mood;
    private final SizeTracker sizeTracker;

    public Plant(int id, Position startingPosition, Mood startingMood, int maxSize, double pollinationProbability, int pollinationPeriod) {
        this.id = id;
        this.position = startingPosition;
        this.mood = startingMood;
        this.pollinationProbability = pollinationProbability;
        this.pollinationPeriod = pollinationPeriod;
        final double minSizeProportion = 0.05;
        int initialSize = (int) Math.round(maxSize * RandomProvider.nextDouble(minSizeProportion, 1));
        this.sizeTracker = new SizeTracker(initialSize, maxSize);
        this.maxSize = maxSize;
    }

    @Override
    public VitalSign tick() {
        LOG.debug("#### Updating state of {} ####", this);
        Mood oldMood = mood;
        mood = oldMood.tick(this);
        if (oldMood != mood) {
            LOG.info("Transitioning from {} to {}", oldMood, mood);
        }
        int sizeBefore = sizeTracker.getValue();
        sizeTracker.update(oldMood.getEnergyDelta());
        if (sizeTracker.getValue() != sizeBefore) {
            LOG.debug("Size went from {} to {}", sizeBefore, sizeTracker.getValue());
        }

        return sizeTracker.isAlive() ? null : sizeTracker;
    }

    @Override
    public Mood getMood() {
        return mood;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public AgentDetails.Immutable getDetails() {
        AgentDetails details = new AgentDetails(id, getClass().getSimpleName(), position());
        details.addAttribute(VitalSign.PARAMETER_PREFIX + "maxSize", maxSize);
        details.addAttribute(Mood.PARAMETER_PREFIX + "current", mood.getClass().getSimpleName());

        details.addAllDetails(mood.getDetails());
        details.addAllDetails(sizeTracker.getDetails());

        return details.toImmutable();
    }

    @Override
    public Position.Immutable position() {
        return position.toImmutable();
    }

    @Override
    public String toString() {
        return "Plant{" +
                "id=" + id +
                ", position=" + position +
                ", mood=" + mood +
                '}';
    }

    @Override
    public int transferEnergy(int desiredBiteSize) {
        if (!(mood instanceof BeingEaten)) {
            mood = new BeingEaten(id);
        }

        double plantEnergyProportion = desiredBiteSize / (double) maxSize;
        int energyCost = (int) Math.round(plantEnergyProportion * VitalSign.MAX_ENERGY);
        energyCost = Math.min(energyCost, sizeTracker.getValue());

        ((BeingEaten) mood).bite(energyCost);
        LOG.debug("Plant {} lost {} energy", id, energyCost);

        int actualBiteSize = (int) Math.round((energyCost / (double) VitalSign.MAX_ENERGY) * maxSize);
        return Math.min(desiredBiteSize, actualBiteSize);
    }

    public boolean isFullSize() {
        return sizeTracker.isFullSize();
    }

    public double getPollinationProbability() {
        return pollinationProbability;
    }

    public int getPollinationPeriod() {
        return pollinationPeriod;
    }
    public int getMaxSize() {
        return sizeTracker.getMaxSize();
    }
}
