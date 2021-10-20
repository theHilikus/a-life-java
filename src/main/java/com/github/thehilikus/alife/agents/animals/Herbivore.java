package com.github.thehilikus.alife.agents.animals;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.animals.moods.Existing;
import com.github.thehilikus.alife.agents.animals.motions.Legs;
import com.github.thehilikus.alife.agents.animals.motions.StraightWalkWithRandomTurn;
import com.github.thehilikus.alife.agents.animals.visions.SurroundingsVision;
import com.github.thehilikus.alife.agents.controllers.*;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.IdsProvider;
import com.github.thehilikus.alife.world.RandomProvider;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A living agent that eats {@link com.github.thehilikus.alife.agents.plants.Plant}
 */
public class Herbivore implements Agent.Movable, Agent.Evolvable {

    private static final Logger LOG = LoggerFactory.getLogger(Herbivore.class.getSimpleName());
    private final Vision vision;
    private final Locomotion locomotion;

    private final Genome genome;

    private final int id;
    private Mood mood;
    private final VitalsController vitals;

    public static void create(int count, World world) {
        for (int current = 0; current < count; current++) {
            int id = IdsProvider.getNextId();
            Genome genome = new Herbivore.HerbivoreGenome(id);
            Vision vision = new SurroundingsVision(id, genome, world);
            Legs legs = new Legs(id, world.getEmptyPosition(), genome);
            Locomotion locomotion = new StraightWalkWithRandomTurn(id, legs, genome);
            Mood startingMood = new Existing(vision, genome, locomotion);

            HungerTracker hungerTracker = new HungerTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "hungryThreshold"));
            EnergyTracker energyTracker = new EnergyTracker(id, genome.getGene(VitalSign.PARAMETER_PREFIX + "lowEnergyThreshold"));
            AgeTracker ageTracker = new AgeTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "lifeExpectancy"));
            ReproductionTracker reproductionTracker = new ReproductionTracker();
            MoodController moodController = new HerbivoreMoodController(vision, legs, locomotion, genome, hungerTracker, energyTracker, ageTracker, reproductionTracker, world);
            VitalsController vitals = new VitalsController(id, moodController, hungerTracker, energyTracker, ageTracker, reproductionTracker);

            Living newAgent = new Herbivore(id, vision, locomotion, startingMood, genome, vitals);
            LOG.info("Created {}", newAgent);
            world.addAgent(newAgent);
        }
    }

    private Herbivore(int id, Vision vision, Locomotion locomotion, Mood startingMood, Genome genome, VitalsController vitals) {
        this.id = id;
        this.vision = vision;
        this.locomotion = locomotion;
        this.genome = genome;
        this.vitals = vitals;
        mood = startingMood;
    }

    @Override
    public VitalSign tick() {
        LOG.debug("#### Updating state of {} ####", this);
        Mood oldMood = mood;
        mood = oldMood.tick();
        Mood priorityMood = vitals.update(oldMood, mood);
        boolean alive = vitals.isAlive();
        if (alive && !priorityMood.getClass().equals(mood.getClass())) {
            LOG.info("Overwriting mood {} with {} due to vital need", mood, priorityMood);
            mood = priorityMood;
        } else if (oldMood != mood) {
            LOG.info("Transitioning from {} to {}", oldMood, mood);
        }

        return alive ? null : vitals.getCauseOfDeath();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Map<String, String> getDetails() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("type", getClass().getSimpleName());
        result.put("position", locomotion.getPosition().getX() + ", " + locomotion.getPosition().getY());
        result.putAll(vitals.getParameters());
        result.putAll(mood.getParameters());
        result.putAll(vision.getParameters());
        result.putAll(locomotion.getParameters());
//        result.putAll(genome.getParameters());

        return result;
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
        return "Herbivore{" +
                "id=" + id +
                ", position=" + locomotion.getPosition() +
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
    public void changePosition(Position newPosition, Orientation direction) {
        locomotion.setPosition(newPosition, direction);
    }

    public static class HerbivoreGenome extends Genome {
        private static final int MIN_VISION_DISTANCE = 2;
        private static final int MAX_VISION_DISTANCE = 20;
        private static final int MIN_TOP_SPEED = 1;
        private static final double MAX_IDLE_SPEED_FACTOR = 0.25;
        private static final double MAX_SCOUT_SPEED_FACTOR = 0.5;
        private static final double MAX_HUNT_SPEED_FACTOR = 0.9;
        private static final int MAX_SIZE = 50;
        private static final int MAX_LIFE_EXPECTANCY = 150;
        private static final int MIN_LIFE_EXPECTANCY = 50;
        private static final int MAX_LOW_ENERGY_THRESHOLD = 50;
        private static final int MAX_HUNGRY_THRESHOLD = 50;
        private static final int MIN_TEEN_AGE = 10;
        private static final int MAX_TEEN_AGE = 20;
        private static final int MAX_MATING_DURATION = 7;
        private static final int MIN_MATING_DURATION = 1;

        private static Map<String, Object> createGenes() {
            int visionDistance = RandomProvider.nextInt(MIN_VISION_DISTANCE, MAX_VISION_DISTANCE);
            return Map.ofEntries(
                    Map.entry("type", "Herbivore"),
                    Map.entry("size", RandomProvider.nextInt(MAX_SIZE)),
                    Map.entry(Vision.PARAMETER_PREFIX + "radius", visionDistance),
                    Map.entry(Locomotion.PARAMETER_PREFIX + "topSpeed", RandomProvider.nextInt(MIN_TOP_SPEED, visionDistance)), //agent can't move further than it can see
                    Map.entry(Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor", RandomProvider.nextDouble(1) * -1),
                    Map.entry(Locomotion.PARAMETER_PREFIX + "turningProbability", RandomProvider.nextDouble(1)),
                    Map.entry(Locomotion.PARAMETER_PREFIX + "idleSpeedFactor", RandomProvider.nextDouble(MAX_IDLE_SPEED_FACTOR)),
                    Map.entry(Locomotion.PARAMETER_PREFIX + "scoutSpeedFactor", RandomProvider.nextDouble(MAX_SCOUT_SPEED_FACTOR)),
                    Map.entry(Locomotion.PARAMETER_PREFIX + "huntSpeedFactor", RandomProvider.nextDouble(MAX_HUNT_SPEED_FACTOR)),
                    Map.entry(VitalSign.PARAMETER_PREFIX + "lifeExpectancy", RandomProvider.nextInt(MIN_LIFE_EXPECTANCY, MAX_LIFE_EXPECTANCY)),
                    Map.entry(VitalSign.PARAMETER_PREFIX + "lowEnergyThreshold", RandomProvider.nextInt(MAX_LOW_ENERGY_THRESHOLD)),
                    Map.entry(VitalSign.PARAMETER_PREFIX + "hungryThreshold", RandomProvider.nextInt(MAX_HUNGRY_THRESHOLD)),
                    Map.entry(Agent.Evolvable.PARAMETER_PREFIX + "teenAge", RandomProvider.nextInt(MIN_TEEN_AGE, MAX_TEEN_AGE)),
                    Map.entry(Agent.Evolvable.PARAMETER_PREFIX + "matingDuration", RandomProvider.nextInt(MIN_MATING_DURATION, MAX_MATING_DURATION))
            );
        }

        HerbivoreGenome(int agentId) {
            super(agentId, createGenes());
        }
    }
}
