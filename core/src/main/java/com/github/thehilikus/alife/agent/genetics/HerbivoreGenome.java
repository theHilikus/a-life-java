package com.github.thehilikus.alife.agent.genetics;

import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.vision.api.Vision;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Genetic material for an {@link com.github.thehilikus.alife.agent.social.Herbivore}
 */
public class HerbivoreGenome extends Genome {
    private static final Logger LOG = LoggerFactory.getLogger(HerbivoreGenome.class.getSimpleName());
    private static final double CROSSOVER_PROBABILITY = 0.7;
    private static final double MUTATION_PROBABILITY = 0.05;

    private static final int MIN_VISION_DISTANCE = 5;
    private static final int MIN_TOP_SPEED = 1;
    private static final double MAX_IDLE_SPEED_FACTOR = 0.25;
    private static final double MAX_SCOUT_SPEED_FACTOR = 0.5;
    private static final double MAX_HUNT_SPEED_FACTOR = 0.9;
    private static final int MAX_LIFE_EXPECTANCY = 150;
    private static final int MIN_LIFE_EXPECTANCY = 50;
    private static final int MAX_LOW_ENERGY_THRESHOLD = 50;
    private static final int MAX_HUNGRY_THRESHOLD = 50;
    private static final double MIN_TEEN_AGE_PROPORTION = 0.1;
    private static final double MAX_TEEN_AGE_PROPORTION = 0.5;
    private static final int MAX_MATING_DURATION = 7;
    private static final int MIN_MATING_DURATION = 1;
    private static int maxVisionDistance;
    private static int maxSize;

    private static Map<String, Object> createGenes() {
        Map<String, Object> result = new HashMap<>();
        int size = RandomProvider.nextInt(LivingAgent.MIN_SIZE, maxSize);
        result.put("maxSize", size);
        int visionDistance = RandomProvider.nextInt(Math.max(MIN_VISION_DISTANCE, size), maxVisionDistance); //vision should cover at least its body
        result.put("type", "Herbivore");
        result.put(Vision.PARAMETER_PREFIX + "distance", visionDistance);
        result.put(Locomotion.PARAMETER_PREFIX + "topSpeed", RandomProvider.nextInt(MIN_TOP_SPEED, visionDistance)); //agent can't move further than it can see
        result.put(Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor", RandomProvider.nextDouble(1) * -1);
        result.put(Locomotion.PARAMETER_PREFIX + "turningProbability", RandomProvider.nextDouble(1));
        result.put(Locomotion.PARAMETER_PREFIX + "idleSpeedFactor", RandomProvider.nextDouble(MAX_IDLE_SPEED_FACTOR));
        result.put(Locomotion.PARAMETER_PREFIX + "scoutSpeedFactor", RandomProvider.nextDouble(MAX_SCOUT_SPEED_FACTOR));
        result.put(Locomotion.PARAMETER_PREFIX + "huntSpeedFactor", RandomProvider.nextDouble(MAX_HUNT_SPEED_FACTOR));
        int lifeExpectancy = RandomProvider.nextInt(MIN_LIFE_EXPECTANCY, MAX_LIFE_EXPECTANCY);
        result.put(VitalSign.PARAMETER_PREFIX + "lifeExpectancy", lifeExpectancy);
        result.put(VitalSign.PARAMETER_PREFIX + "lowEnergyThreshold", RandomProvider.nextInt(MAX_LOW_ENERGY_THRESHOLD));
        result.put(VitalSign.PARAMETER_PREFIX + "hungryThreshold", RandomProvider.nextInt(MAX_HUNGRY_THRESHOLD));
        result.put(VitalSign.PARAMETER_PREFIX + "teenAge", (int) (lifeExpectancy * RandomProvider.nextDouble(MIN_TEEN_AGE_PROPORTION, MAX_TEEN_AGE_PROPORTION)));
        result.put(VitalSign.PARAMETER_PREFIX + "matingDuration", RandomProvider.nextInt(MIN_MATING_DURATION, MAX_MATING_DURATION));

        return result;
    }

    public HerbivoreGenome() {
        this(createGenes());
    }

    private HerbivoreGenome(Map<String, Object> genes) {
        super(genes);
    }

    public static void configureEnvironmentalLimits(World world) {
        maxVisionDistance = Math.max(world.getWidth(), world.getHeight()) / 2; //max vision is half the world
        final double maxSizeProportionToWorld = 0.05;
        maxSize = (int) (Math.min(world.getWidth(), world.getHeight()) * maxSizeProportionToWorld);
        maxSize = Math.max(maxSize, LivingAgent.MIN_SIZE + 1); //to allow a valid RNG range when world is small
    }

    @Override
    public Genome crossover(Genome maternalGenome) {
        double random = RandomProvider.nextDouble(1);
        Genome result;
        if (random < CROSSOVER_PROBABILITY) {
            result = singlePointCrossover(maternalGenome);
        } else {
            boolean cloneMother = RandomProvider.nextBoolean();
            if (cloneMother) {
                result = new HerbivoreGenome(new HashMap<>(maternalGenome.getGenes()));
            } else {
                result = new HerbivoreGenome(new HashMap<>(getGenes()));
            }
            LOG.debug("No crossover. Instead, cloning the genome of the " + (cloneMother ? "mother" : "father"));
        }

        return result;
    }

    private Genome singlePointCrossover(Genome maternalGenome) {
        Iterator<Map.Entry<String, Object>> paternalGenesIter = getGenes().entrySet().iterator();
        Iterator<Map.Entry<String, Object>> maternalGenesIter = maternalGenome.getGenes().entrySet().iterator();

        Map<String, Object> offspringGenes = new HashMap<>();
        int splitPoint = RandomProvider.nextInt(1, getGenes().size());
        LOG.debug("Performing single-point crossover with split point = {}", splitPoint);
        for (int pos = 0; pos < getGenes().size(); pos++) {
            Map.Entry<String, Object> paternalGene = paternalGenesIter.next();
            Map.Entry<String, Object> maternalGene = maternalGenesIter.next();
            if (!paternalGene.getKey().equals(maternalGene.getKey())) {
                throw new IllegalStateException("Genes are not properly aligned");
            }
            if (pos < splitPoint) {
                offspringGenes.put(maternalGene.getKey(), maternalGene.getValue());
            } else {
                offspringGenes.put(paternalGene.getKey(), paternalGene.getValue());
            }
        }

        return new HerbivoreGenome(offspringGenes);
    }

    @Override
    public void mutate() {
        Map<String, Object> mutationsPool = createGenes();

        Iterator<Map.Entry<String, Object>> currentGenesIter = getGenes().entrySet().iterator();
        Iterator<Map.Entry<String, Object>> mutationsPoolIter = mutationsPool.entrySet().iterator();

        for (int pos = 0; pos < getGenes().size(); pos++) {
            Map.Entry<String, Object> currentGene = currentGenesIter.next();
            Map.Entry<String, Object> mutatedGene = mutationsPoolIter.next();
            if (!currentGene.getKey().equals(mutatedGene.getKey())) {
                throw new IllegalStateException("Genes are not properly aligned");
            }
            if (RandomProvider.nextDouble(1) < MUTATION_PROBABILITY) {
                getGenes().put(mutatedGene.getKey(), mutatedGene.getValue());
            }
        }
    }
}
