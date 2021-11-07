package com.github.thehilikus.alife.agents.animals;

import com.github.thehilikus.alife.agents.animals.moods.Existing;
import com.github.thehilikus.alife.agents.animals.motions.StraightWalkWithRandomTurn;
import com.github.thehilikus.alife.agents.animals.visions.SurroundingsVision;
import com.github.thehilikus.alife.agents.controllers.*;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.agents.genetics.HerbivoreGenome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.IdsProvider;
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
        HerbivoreGenome.configureEnvironmentalLimits(world);

        for (int current = 0; current < count; current++) {
            int id = IdsProvider.getNextId();
            Genome genome = new HerbivoreGenome();
            Vision vision = new SurroundingsVision(id, genome, world);
            Locomotion locomotion = new StraightWalkWithRandomTurn(id, world.getRandomPosition(), genome);
            Mood startingMood = new Existing(vision, genome, locomotion);

            HungerTracker hungerTracker = new HungerTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "hungryThreshold"));
            EnergyTracker energyTracker = new EnergyTracker(id, genome.getGene(VitalSign.PARAMETER_PREFIX + "lowEnergyThreshold"));
            AgeTracker ageTracker = new AgeTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "lifeExpectancy"));
            ReproductionTracker reproductionTracker = new ReproductionTracker();
            SizeTracker sizeTracker = new SizeTracker(genome.getGene("maxSize"));
            MoodController moodController = new HerbivoreMoodController(vision, locomotion, genome, hungerTracker, energyTracker, ageTracker, reproductionTracker, sizeTracker, world);
            VitalsController vitals = new VitalsController(id, moodController, hungerTracker, energyTracker, ageTracker, reproductionTracker, sizeTracker);

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
    public Map<String, Object> getDetails() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("type", getClass().getSimpleName());
        result.put("position", getPosition());
        result.put(Mood.PARAMETER_PREFIX + "current", mood.getClass().getSimpleName());

        result.putAll(vitals.getParameters());
        result.putAll(mood.getParameters());
        result.putAll(vision.getParameters());
        result.putAll(locomotion.getParameters());
        result.putAll(genome.getParameters());

        return result;
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
    public Evolvable reproduce(int fatherId, World world, Genome offspringGenome) {
        int id = IdsProvider.getNextId();
        Vision vision = new SurroundingsVision(id, offspringGenome, world);
        Position offspringPosition = new Position(getPosition().getX(), getPosition().getY());
        Locomotion offspringLocomotion = new StraightWalkWithRandomTurn(id, offspringPosition, offspringGenome);
        Mood offspringStartingMood = new Existing(vision, offspringGenome, offspringLocomotion);

        HungerTracker offspringHungerTracker = new HungerTracker(offspringGenome.getGene(VitalSign.PARAMETER_PREFIX + "hungryThreshold"));
        EnergyTracker offspringEnergyTracker = new EnergyTracker(id, offspringGenome.getGene(VitalSign.PARAMETER_PREFIX + "lowEnergyThreshold"));
        AgeTracker offspringAgeTracker = new AgeTracker(offspringGenome.getGene(VitalSign.PARAMETER_PREFIX + "lifeExpectancy"));
        ReproductionTracker offspringReproductionTracker = new ReproductionTracker();
        SizeTracker offspringSizeTracker = new SizeTracker(offspringGenome.getGene("maxSize"));
        MoodController offspringMoodController = new HerbivoreMoodController(vision, offspringLocomotion, offspringGenome, offspringHungerTracker, offspringEnergyTracker, offspringAgeTracker, offspringReproductionTracker, offspringSizeTracker, world);
        VitalsController offspringVitals = new VitalsController(id, offspringMoodController, offspringHungerTracker, offspringEnergyTracker, offspringAgeTracker, offspringReproductionTracker, offspringSizeTracker);

        Evolvable result = new Herbivore(id, vision, offspringLocomotion, offspringStartingMood, offspringGenome, offspringVitals);
        LOG.info("Created offspring {}", result);
        world.addAgent(result);

        vitals.gaveBirth(fatherId, result);
        return result;
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
