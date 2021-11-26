package com.github.thehilikus.alife.agent.factories;

import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.api.LivingAgentFactory;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.api.internal.IdsProvider;
import com.github.thehilikus.alife.agent.controllers.SocialController;
import com.github.thehilikus.alife.agent.controllers.VitalsController;
import com.github.thehilikus.alife.agent.genetics.BoidGenome;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.moods.Flocking;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.motion.Wings;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.social.Boid;
import com.github.thehilikus.alife.agent.vision.SurroundingsVision;
import com.github.thehilikus.alife.agent.vision.api.Vision;
import com.github.thehilikus.alife.agent.vitals.*;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoidFactory extends LivingAgentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(BoidFactory.class.getSimpleName());
    private static boolean configuredGenome;

    @Override
    protected LivingAgent createAgent() {
        if (!configuredGenome) {
            BoidGenome.configureEnvironmentalLimits(getWorld());
            configuredGenome = true;
        }
        Genome genome = new BoidGenome();
        Boid result = createAgentFromGenome(genome, new Position(200, 200));
        LOG.info("Created {}", result);

        return result;
    }

    private Boid createAgentFromGenome(Genome genome, Position position) {
        int id = IdsProvider.getNextId();
        AgentModules dependencies = new AgentModules(genome);
        dependencies.addComponent(Vision.class, new SurroundingsVision(id, genome, getWorld()));
        dependencies.addComponent(Locomotion.class, new Wings(getWorld().getWidth(), getWorld().getHeight(), id, new Position(position.getX()+id*20-300, position.getY()), genome));

        dependencies.addVitalSign(HungerTracker.class, new HungerTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "hungryThreshold")));
        dependencies.addVitalSign(EnergyTracker.class, new EnergyTracker(id, genome.getGene(VitalSign.PARAMETER_PREFIX + "lowEnergyThreshold")));
        dependencies.addVitalSign(AgeTracker.class, new AgeTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "teenAge"), genome.getGene(VitalSign.PARAMETER_PREFIX + "lifeExpectancy")));
        dependencies.addVitalSign(ReproductionTracker.class, new ReproductionTracker());
        dependencies.addVitalSign(SizeTracker.class, new SizeTracker(genome.getGene("maxSize")));

        VitalsController vitalsController = new VitalsController(id, dependencies);
        SocialController socialController = new SocialController(id, dependencies, vitalsController);

        Mood startingMood = new Flocking(dependencies);
        Boid result = new Boid(id, dependencies, startingMood, vitalsController, socialController);

        getWorld().addAgent(result);

        return result;
    }
}
