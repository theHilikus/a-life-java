package com.github.thehilikus.alife.agent.genetics;

import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.vision.api.Vision;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
import com.github.thehilikus.alife.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Genetic material for an {@link com.github.thehilikus.alife.agent.social.Herbivore}
 */
public class HerbivoreGenome extends Genome {
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
    private static final double MAX_ENERGY_EXPENDITURE_FACTOR = 0.20;
    private static int maxVisionDistance;
    private static int maxSize;

    public HerbivoreGenome() {
        super();
    }

    private HerbivoreGenome(Map<String, Object> genes) {
        super(genes);
    }

    @Override
    protected Genome create(Map<String, Object> genes) {
        return new HerbivoreGenome(genes);
    }

    @Override
    protected Map<String, Object> createGenes() {
        Map<String, Object> result = new HashMap<>();
        int maxSize = RandomProvider.nextInt(LivingAgent.MIN_SIZE, HerbivoreGenome.maxSize);
        result.put("maxSize", maxSize);
        int visionDistance = RandomProvider.nextInt(Math.max(MIN_VISION_DISTANCE, maxSize), maxVisionDistance); //vision should cover at least its body
        result.put("type", "Herbivore");
        result.put(Vision.PARAMETER_PREFIX + "distance", visionDistance);
        result.put(Locomotion.PARAMETER_PREFIX + "topSpeed", RandomProvider.nextInt(MIN_TOP_SPEED, visionDistance)); //agent can't move further than it can see
        result.put(Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor", RandomProvider.nextDouble(MAX_ENERGY_EXPENDITURE_FACTOR) * -1);
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

    public static void configureEnvironmentalLimits(World world) {
        maxVisionDistance = Math.max(world.getWidth(), world.getHeight()) / 2; //max vision is half the world
        final double maxSizeProportionToWorld = 0.05;
        maxSize = (int) (Math.min(world.getWidth(), world.getHeight()) * maxSizeProportionToWorld);
        maxSize = Math.max(maxSize, LivingAgent.MIN_SIZE + 1); //to allow a valid RNG range when world is small
    }
}
