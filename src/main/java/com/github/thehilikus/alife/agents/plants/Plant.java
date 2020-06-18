package com.github.thehilikus.alife.agents.plants;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.IdsSource;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An inanimate agent that gets eaten
 */
public class Plant implements Agent {
    private static final Logger LOG = LoggerFactory.getLogger(Plant.class.getSimpleName());
    private final int id;
    private final Position position;
    private final Locomotion locomotion;
    private final Genome genome;

    public static void create(int count, World world) {
        for (int current = 0; current < count; current++) {
            int id = IdsSource.getNextId();
            Position startingPosition = world.getEmptyPosition();
            Locomotion locomotion = new NoLocomotion(id, startingPosition);
            Genome genome = new PlantGenome(id);

            Agent newAgent = new Plant(id, startingPosition, locomotion, genome);
            LOG.info("Created {}", newAgent);
            world.addAgent(newAgent);
        }
    }

    private Plant(int id, Position position, Locomotion locomotion, Genome genome) {
        this.id = id;
        this.position = position;
        this.locomotion = locomotion;
        this.genome = genome;

    }

    @Override
    public void tick() {
        LOG.debug("#### Updating state of {} ####", this);
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
        result.putAll(locomotion.getParameters());

        return result;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public String getStringRepresentation() {
        Ansi.Attribute agentTypeStyle = Ansi.Attribute.NONE;

        Ansi.BColor background = Ansi.BColor.GREEN;
        String formatCode = Ansi.generateCode(agentTypeStyle, Ansi.FColor.WHITE, background);
        String idString = Integer.toString(id);
        if (id < 10) {
            idString = ' ' + idString;
        }
        return Ansi.formatMessage(idString, formatCode);
    }

    @Override
    public Genome getGenome() {
        return genome;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "id=" + id +
                ", position=" + position +
                '}';
    }

    private static class PlantGenome extends Genome {
        public PlantGenome(int agentId) {
            super(agentId, Collections.emptyMap());
        }
    }
}
