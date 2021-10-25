package com.github.thehilikus.alife.agents.plants;

import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.plants.moods.BeingEaten;
import com.github.thehilikus.alife.agents.plants.moods.Growing;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.IdsProvider;
import com.github.thehilikus.alife.world.RandomProvider;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An inanimate agent that gets eaten
 */
public class Plant implements Agent.Eatable {
    private static final Logger LOG = LoggerFactory.getLogger(Plant.class.getSimpleName());
    private final int id;
    private Position position;
    private final int size;
    private Mood mood;
    private final EnergyTracker energyTracker;

    public static void create(int count, World world) {
        for (int current = 0; current < count; current++) {
            int id = IdsProvider.getNextId();
            Mood startingMood = new Growing(id);

            Agent.Living newAgent = new Plant(id, world.getEmptyPosition(), startingMood);
            LOG.info("Created {}", newAgent);
            world.addAgent(newAgent);
        }
    }

    private Plant(int id, Position startingPosition, Mood startingMood) {
        this.id = id;
        this.position = startingPosition;
        this.mood = startingMood;
        this.energyTracker = new EnergyTracker(id);
        this.size = RandomProvider.nextInt(MAX_SIZE);
    }

    @Override
    public VitalSign tick() {
        LOG.debug("#### Updating state of {} ####", this);
        Mood oldMood = mood;
        mood = oldMood.tick();
        if (oldMood != mood) {
            LOG.info("Transitioning from {} to {}", oldMood, mood);
        }
        int energyBefore = energyTracker.getValue();
        energyTracker.update(oldMood);
        if (energyTracker.getValue() != energyBefore) {
            LOG.debug("Energy went from {} to {}", energyBefore, energyTracker.getValue());
        }

        return energyTracker.isAlive() ? null : energyTracker;
    }

    @Override
    public void changePosition(Position newPosition, Orientation direction) {
        position = newPosition;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Map<String, Object> getDetails() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", getClass().getSimpleName());
        result.put("size", size);
        result.put("position", position.getX() + ", " + position.getY());
        result.put(Mood.PARAMETER_PREFIX + "current", mood.getClass().getSimpleName());

        result.putAll(mood.getParameters());
        result.putAll(energyTracker.getParameters());

        return result;
    }

    @Override
    public Position.Immutable getPosition() {
        return position.toImmutable();
    }

    @Override
    public String toString() {
        return "Plant{" +
                "id=" + id +
                ", position=" + position +
                '}';
    }

    @Override
    public int transferEnergy(int desiredBiteSize) {
        if (!(mood instanceof BeingEaten)) {
            mood = new BeingEaten(id);
        }

        double plantEnergyProportion = desiredBiteSize / (double) size;
        int energyCost = (int) Math.round(plantEnergyProportion * EnergyTracker.MAX_ENERGY);
        energyCost = Math.min(energyCost, energyTracker.getValue());

        ((BeingEaten) mood).bite(energyCost);
        LOG.debug("Plant lost {} energy", energyCost);

        int actualBiteSize = (int) Math.round((energyCost / (double) EnergyTracker.MAX_ENERGY) * size);
        return Math.min(desiredBiteSize, actualBiteSize);
    }
}
