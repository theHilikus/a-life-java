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
    private final Locomotion locomotion;
    private final Genome genome;

    public static void create(int count, World world) {
        for (int current = 0; current < count; current++) {
            int id = IdsSource.getNextId();
            Locomotion locomotion = new NoLocomotion(id, world);
            Genome genome = new PlantGenome(id);

            Agent newAgent = new Plant(id, locomotion, genome);
            LOG.info("Created {}", newAgent);
            world.addAgent(newAgent);
        }
    }

    private Plant(int id, Locomotion locomotion, Genome genome) {
        this.id = id;
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
        return locomotion.getParameters();
    }

    @Override
    public Coordinates.Immutable getPosition() {
        return locomotion.getPosition();
    }

    @Override
    public String getStringRepresentation() {
        Ansi.Attribute agentTypeStyle = Ansi.Attribute.LIGHT;

        Ansi.BColor background = Ansi.BColor.GREEN;
        String formatCode = Ansi.generateCode(agentTypeStyle, Ansi.FColor.BLACK, background);
        return Ansi.formatMessage(String.format("%02d", id), formatCode);
    }

    @Override
    public Genome getGenome() {
        return genome;
    }

    @Override
    public String toString() {
        return "FoodAgent{" +
                "id=" + id +
                '}';
    }

    private static class PlantGenome extends Genome {
        public PlantGenome(int agentId) {
            super(agentId, Collections.emptyMap());
        }
    }
}
