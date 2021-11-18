package com.github.thehilikus.alife.agent.plants;

import com.github.thehilikus.alife.agent.vitals.EnergyTracker;
import com.github.thehilikus.alife.agent.moods.BeingEaten;
import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
import com.github.thehilikus.alife.agent.api.RandomProvider;
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
    private final Position position;
    private final int size;
    private Mood mood;
    private final EnergyTracker energyTracker;

    Plant(int id, Position startingPosition, Mood startingMood, int maxSize) {
        this.id = id;
        this.position = startingPosition;
        this.mood = startingMood;
        this.energyTracker = new EnergyTracker(id);
        this.size = RandomProvider.nextInt(Agent.MIN_SIZE, Math.max(maxSize, Agent.MIN_SIZE + 1));
    }

    @Override
    public VitalSign tick() {
        LOG.debug("#### Updating state of {} ####", this);
        Mood oldMood = mood;
        mood = oldMood.tick(this);
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
    public int getId() {
        return id;
    }

    @Override
    public Map<String, Object> getDetails() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("type", getClass().getSimpleName());
        result.put("size", size);
        result.put("position", position.toImmutable());
        result.put(Mood.PARAMETER_PREFIX + "current", mood.getClass().getSimpleName());

        result.putAll(mood.getDetails());
        result.putAll(energyTracker.getDetails());

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
        LOG.debug("Plant {} lost {} energy", id, energyCost);

        int actualBiteSize = (int) Math.round((energyCost / (double) EnergyTracker.MAX_ENERGY) * size);
        return Math.min(desiredBiteSize, actualBiteSize);
    }
}
