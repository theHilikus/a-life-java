package com.github.thehilikus.alife.agents.animals;

import com.github.thehilikus.alife.agents.LivingAgentFactory;
import com.github.thehilikus.alife.agents.animals.moods.Existing;
import com.github.thehilikus.alife.agents.animals.motions.StraightWalkWithRandomTurn;
import com.github.thehilikus.alife.agents.animals.visions.SurroundingsVision;
import com.github.thehilikus.alife.agents.controllers.*;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.agents.genetics.HerbivoreGenome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.IdsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constructor for {@link Herbivore}
 */
public class HerbivoreFactory extends LivingAgentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HerbivoreFactory.class);
    private static boolean configuredGenome;

    @Override
    public Agent.Living createAgent() {
        if (!configuredGenome) {
            HerbivoreGenome.configureEnvironmentalLimits(getWorld());
            configuredGenome = true;
        }
        Genome genome = new HerbivoreGenome();
        Herbivore result = createAgentFromGenome(genome, getWorld().getRandomPosition());
        LOG.info("Created {}", result);

        return result;
    }

    private Herbivore createAgentFromGenome(Genome genome, Position position) {
        int id = IdsProvider.getNextId();
        Vision vision = new SurroundingsVision(id, genome, getWorld());
        Locomotion locomotion = new StraightWalkWithRandomTurn(id, position, genome);
        Mood startingMood = new Existing(vision, genome, locomotion);
        VitalsController vitalsController = createVitalsController(id, genome, vision, locomotion);

        Herbivore result = new Herbivore(id, genome, vision, locomotion, startingMood, vitalsController);
        getWorld().addAgent(result);

        return result;
    }

    private VitalsController createVitalsController(int id, Genome genome, Vision vision, Locomotion locomotion) {
        HungerTracker hungerTracker = new HungerTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "hungryThreshold"));
        EnergyTracker energyTracker = new EnergyTracker(id, genome.getGene(VitalSign.PARAMETER_PREFIX + "lowEnergyThreshold"));
        AgeTracker ageTracker = new AgeTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "lifeExpectancy"));
        ReproductionTracker reproductionTracker = new ReproductionTracker();
        SizeTracker sizeTracker = new SizeTracker(genome.getGene("maxSize"));
        MoodController moodController = new HerbivoreMoodController(vision, locomotion, genome, hungerTracker, energyTracker, ageTracker, reproductionTracker, sizeTracker);

        return new VitalsController(id, moodController, hungerTracker, energyTracker, ageTracker, reproductionTracker, sizeTracker);
    }

    public Agent.Living createOffspring(int motherId, Genome motherGenome, Genome father) {
        Genome offspringGenome = motherGenome.crossover(father);
        offspringGenome.mutate();

        Agent.Living mother = getWorld().getLivingAgent(motherId).orElseThrow();
        Position offspringPosition = new Position(mother.getPosition().getX(), mother.getPosition().getY());
        Herbivore result = createAgentFromGenome(offspringGenome, offspringPosition);
        LOG.info("Created offspring {}", result);
        return result;
    }
}
