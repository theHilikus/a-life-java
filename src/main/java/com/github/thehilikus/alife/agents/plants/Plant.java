package com.github.thehilikus.alife.agents.plants;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.plants.moods.BeingEaten;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.RandomProvider;
import com.github.thehilikus.alife.world.WorldComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An inanimate agent that gets eaten
 */
public class Plant implements Agent.Eatable {
    private static final Logger LOG = LoggerFactory.getLogger(Plant.class.getSimpleName());
    private static final int MAX_SIZE = 300;
    private final int id;
    private final Position position;
    private final int size;
    private Mood mood;
    private final EnergyTracker energyTracker;

    public static void create(int count, WorldComponent worldComponent) {
        for (int current = 0; current < count; current++) {
            LivingAgentComponent livingAgentComponent = DaggerLivingAgentComponent.builder().worldComponent(worldComponent).build();
            Agent.Living newAgent = livingAgentComponent.createPlant();
            LOG.info("Created {}", newAgent);
            worldComponent.createWorld().addAgent(newAgent);
        }
    }

    @Inject
    public Plant(int id, Position position, @Named("plants") Mood startingMood) {
        this.id = id;
        this.position = position;
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
    public int getId() {
        return id;
    }

    @Override
    public Map<String, String> getDetails() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("type", getClass().getSimpleName());
        result.put("position", position.getX() + ", " + position.getY());

        return result;
    }

    @Override
    public Position.Immutable getPosition() {
        return position.toImmutable();
    }

    @Override
    public String getStringRepresentation() {
        Ansi.Attribute agentTypeStyle = Ansi.Attribute.NONE;

        Ansi.FColor moodColour = mood.getTerminalColour();
        Ansi.BColor background = Ansi.BColor.GREEN;
        String formatCode = Ansi.generateCode(agentTypeStyle, moodColour, background);
        String idString = Integer.toString(id);
        if (id < 10) {
            idString = ' ' + idString;
        }

        return Ansi.formatMessage(idString, formatCode);
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
