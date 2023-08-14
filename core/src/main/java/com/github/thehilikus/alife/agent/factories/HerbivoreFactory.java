package com.github.thehilikus.alife.agent.factories;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.api.LivingAgentFactory;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.controllers.SocialController;
import com.github.thehilikus.alife.agent.controllers.VitalsController;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.genetics.HerbivoreGenome;
import com.github.thehilikus.alife.agent.moods.Existing;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.motion.RandomWalk;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.social.Herbivore;
import com.github.thehilikus.alife.agent.vision.SurroundingsVision;
import com.github.thehilikus.alife.agent.vision.api.Vision;
import com.github.thehilikus.alife.agent.vitals.*;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constructor for {@link Herbivore}
 */
public class HerbivoreFactory extends LivingAgentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HerbivoreFactory.class);
    private static boolean configuredGenome;

    @Override
    public LivingAgent createAgent() {
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
        int id = getWorld().getNextId();
        AgentModules dependencies = new AgentModules(genome);
        dependencies.addComponent(Vision.class, new SurroundingsVision(id, genome, getWorld()));
        dependencies.addComponent(Locomotion.class, new RandomWalk(getWorld().getWidth(), getWorld().getHeight(), id, position, genome));

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

    @SuppressWarnings("ClassEscapesDefinedScope")
    public Agent createOffspring(Agent mother, Genome motherGenome, Genome father) {
        Genome offspringGenome = motherGenome.crossover(father);
        offspringGenome.mutate();

        Position offspringPosition = new Position(mother.getPosition().getX(), mother.getPosition().getY());
        Herbivore result = createAgentFromGenome(offspringGenome, offspringPosition);
        LOG.info("Created offspring {}", result);
        return result;
    }
}
