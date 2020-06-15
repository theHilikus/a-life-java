package com.github.thehilikus.alife.agents.animals;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.animals.moods.Scouting;
import com.github.thehilikus.alife.agents.animals.motions.StraightWalkWithRandomTurn;
import com.github.thehilikus.alife.agents.animals.visions.SurroundingsVision;
import com.github.thehilikus.alife.agents.controllers.MoodController;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.IdsSource;
import com.github.thehilikus.alife.world.RandomSource;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A living agent that hunts for food
 */
public class Herbivore implements Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Herbivore.class.getSimpleName());
    private final Vision vision;
    private final Locomotion locomotion;

    private final Genome genome;

    private Mood mood;
    private final int id;

    public static void create(int count, World world) {
        for (int current = 0; current < count; current++) {
            int id = IdsSource.getNextId();
            HerbivoreGenome genome = HerbivoreGenome.create(id);

            Locomotion locomotion = new StraightWalkWithRandomTurn(id, genome, world);
            Vision vision = new SurroundingsVision(id, genome, world);

            Agent newAgent = new Herbivore(id, vision, locomotion, new MoodController(world), genome);
            LOG.info("Created {}", newAgent);
            world.addAgent(newAgent);
        }

    }

    private Herbivore(int id, Vision vision, Locomotion locomotion, MoodController moodController, Genome genome) {
        this.id = id;
        this.vision = vision;
        this.locomotion = locomotion;
        this.genome = genome;

        mood = new Scouting(moodController, vision, locomotion, genome);
    }

    @Override
    public void tick() {
        LOG.debug("#### Updating state of {} ####", this);
        Mood newMood = mood.tick();
        if (!newMood.getClass().equals(mood.getClass())) {
            mood = newMood;
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Map<String, String> getDetails() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("type", getClass().getSimpleName());
        result.putAll(genome.getParameters());
        result.putAll(vision.getParameters());
        result.putAll(locomotion.getParameters());
        result.putAll(mood.getParameters());

        return result;
    }

    @Override
    public Coordinates.Immutable getPosition() {
        return locomotion.getPosition();
    }

    @Override
    public String getStringRepresentation() {
        Ansi.Attribute agentTypeStyle = Ansi.Attribute.BOLD;

        Ansi.FColor moodColour = mood.getTerminalColour();
        Ansi.BColor background = Ansi.BColor.NONE;
        String formatCode = Ansi.generateCode(agentTypeStyle, moodColour, background);
        String idString = Integer.toString(id);
        if (id < 10) {
            idString = ' ' + idString;
        }
        return Ansi.formatMessage(idString, formatCode);
    }

    @Override
    public String toString() {
        return "HuntingAgent{" +
                "id=" + id +
                '}';
    }

    @Override
    public Genome getGenome() {
        return genome;
    }

    private static class HerbivoreGenome extends Genome {
        private static final int MAX_VISION_DISTANCE = 20;
        private static final int MAX_TOP_SPEED = 20;
        private static final double MAX_SCOUT_SPEED_FACTOR = 0.5;
        private static final int MAX_SIZE = 50;

        public static HerbivoreGenome create(int agentId) {
            Map<String, Object> genes = createGenes();
            return new HerbivoreGenome(agentId, genes);
        }

        private static Map<String, Object> createGenes() {
            return Map.ofEntries(
                    Map.entry("type", "Herbivore"),
                    Map.entry("size", RandomSource.nextInt(MAX_SIZE)),
                    Map.entry(Vision.PARAMETER_PREFIX + "radius", RandomSource.nextInt(MAX_VISION_DISTANCE)),
                    Map.entry(Locomotion.PARAMETER_PREFIX + "topSpeed", RandomSource.nextInt(MAX_TOP_SPEED)),
                    Map.entry(Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor", RandomSource.nextDouble(1)),
                    Map.entry(Locomotion.PARAMETER_PREFIX + "turningProbability", RandomSource.nextInt(100)),
                    Map.entry(Locomotion.PARAMETER_PREFIX + "scoutSpeedFactor", RandomSource.nextDouble(MAX_SCOUT_SPEED_FACTOR))
            );
        }

        private HerbivoreGenome(int agentId, Map<String, Object> newGenome) {
            super(agentId, newGenome);
        }

    }
}
