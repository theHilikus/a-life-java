package com.github.thehilikus.alife.agents.animals;

import com.github.thehilikus.alife.agents.LivingAgentFactory;
import com.github.thehilikus.alife.agents.animals.moods.AgentModules;
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
        AgentModules dependencies = new AgentModules(genome);
        dependencies.addComponent(Vision.class, new SurroundingsVision(id, genome, getWorld()));
        dependencies.addComponent(Locomotion.class, new StraightWalkWithRandomTurn(id, position, genome));

        dependencies.addVitalSign(HungerTracker.class, new HungerTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "hungryThreshold")));
        dependencies.addVitalSign(EnergyTracker.class, new EnergyTracker(id, genome.getGene(VitalSign.PARAMETER_PREFIX + "lowEnergyThreshold")));
        dependencies.addVitalSign(AgeTracker.class, new AgeTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "teenAge"), genome.getGene(VitalSign.PARAMETER_PREFIX + "lifeExpectancy")));
        dependencies.addVitalSign(ReproductionTracker.class, new ReproductionTracker());
        dependencies.addVitalSign(SizeTracker.class, new SizeTracker(genome.getGene("maxSize")));

        VitalsController vitalsController = new VitalsController(id, dependencies);
        SocialController socialController = new SocialController(id, dependencies, vitalsController);

        Mood startingMood = new Existing(dependencies);
        Herbivore result = new Herbivore(id, dependencies, startingMood, vitalsController, socialController);
        getWorld().addAgent(result);

        return result;
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
