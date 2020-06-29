package com.github.thehilikus.alife.agents.plants;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.plants.moods.BeingEaten;
import com.github.thehilikus.alife.api.*;
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
    private final int id;
    private final Position position;
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
    }

    @Override
    public VitalSign tick() {
        LOG.debug("#### Updating state of {} ####", this);
        Mood oldMood = mood;
        mood = oldMood.tick();
        if (oldMood != mood) {
            LOG.info("Transitioning from {} to {}", oldMood, mood);
        }
        energyTracker.update(oldMood);

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
    public int transferEnergy(int eatSpeed) {
        if (!(mood instanceof BeingEaten)) {
            mood = new BeingEaten(id);
        }
        int biteSize = Math.min(energyTracker.getValue(), eatSpeed);
        ((BeingEaten) mood).bite(biteSize);

        return biteSize;
    }
}
