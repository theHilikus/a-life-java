package com.github.thehilikus.alife.agent.genetics;

import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.vision.api.Vision;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
import com.github.thehilikus.alife.world.World;

import java.util.HashMap;
import java.util.Map;

public class BoidGenome extends Genome {
    private static int maxVisionDistance;
    private static int maxSize;

    private static final int MAX_SEPARATION = 100;
    private static final int MAX_ALIGNMENT = 100;
    private static final int MAX_COHESION = 100;
    private static final int MIN_TOP_SPEED = 10;
    private static final double IDLE_SPEED_FACTOR = 0.3;
    private static final double ENERGY_EXPENDITURE = -0.03;
    private static final int MIN_LIFE_EXPECTANCY = 20;
    private static final int MAX_LIFE_EXPECTANCY = 100;
    private static final int LOW_ENERGY_THRESHOLD = 20;
    private static final int HUNGRY_THRESHOLD = 15;
    private static final double TEEN_AGE_PROPORTION = 0.05;
    private static final int MATING_DURATION = 1;

    public BoidGenome() {
        super();
    }

    private BoidGenome(Map<String, Object> genes) {
        super(genes);
    }

    public static void configureEnvironmentalLimits(World world) {
        maxVisionDistance = Math.max(world.getWidth(), world.getHeight()) / 3; //max vision is half the world
        final double maxSizeProportionToWorld = 0.03;
        maxSize = (int) (Math.min(world.getWidth(), world.getHeight()) * maxSizeProportionToWorld);
        maxSize = Math.max(maxSize, LivingAgent.MIN_SIZE + 1); //to allow a valid RNG range when world is small
    }

    @Override
    protected Genome create(Map<String, Object> genes) {
        return new BoidGenome(genes);
    }

    @Override
    protected Map<String, Object> createGenes() {
        Map<String, Object> result = new HashMap<>();
        int maxSize = RandomProvider.nextInt(LivingAgent.MIN_SIZE, BoidGenome.maxSize);
        result.put("maxSize", maxSize);
        int visionDistance = RandomProvider.nextInt(Math.max(MIN_VISION_DISTANCE, maxSize), maxVisionDistance); //vision should cover at least its body
        result.put("type", "Boid");
        result.put(Vision.PARAMETER_PREFIX + "distance", visionDistance);
        result.put(Locomotion.PARAMETER_PREFIX + "separation", RandomProvider.nextInt(1, MAX_SEPARATION));
        result.put(Locomotion.PARAMETER_PREFIX + "alignment", RandomProvider.nextInt(1, MAX_ALIGNMENT));
        result.put(Locomotion.PARAMETER_PREFIX + "cohesion", RandomProvider.nextInt(1, MAX_COHESION));
        result.put(Locomotion.PARAMETER_PREFIX + "topSpeed", RandomProvider.nextInt(MIN_TOP_SPEED, visionDistance));
        result.put(Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor", ENERGY_EXPENDITURE);
        result.put(Locomotion.PARAMETER_PREFIX + "idleSpeedFactor", IDLE_SPEED_FACTOR);
        int lifeExpectancy = RandomProvider.nextInt(MIN_LIFE_EXPECTANCY, MAX_LIFE_EXPECTANCY);
        result.put(VitalSign.PARAMETER_PREFIX + "lifeExpectancy", lifeExpectancy);
        result.put(VitalSign.PARAMETER_PREFIX + "lowEnergyThreshold", LOW_ENERGY_THRESHOLD);
        result.put(VitalSign.PARAMETER_PREFIX + "hungryThreshold", HUNGRY_THRESHOLD);
        result.put(VitalSign.PARAMETER_PREFIX + "teenAge", (int) (lifeExpectancy * TEEN_AGE_PROPORTION));
        result.put(VitalSign.PARAMETER_PREFIX + "matingDuration", MATING_DURATION);

        return result;
    }
}
