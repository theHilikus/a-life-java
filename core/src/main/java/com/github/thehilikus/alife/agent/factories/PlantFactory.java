package com.github.thehilikus.alife.agent.factories;

import com.github.thehilikus.alife.agent.api.*;
import com.github.thehilikus.alife.agent.moods.Growing;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.plants.Plant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Builds {@link Plant}
 */
public class PlantFactory extends LivingAgentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PlantFactory.class);
    private static final double MAX_POLLINATION_PROBABILITY = 0.05;
    private static final double MAX_DISPERSION_WORLD_PROPORTION = 0.40;
    private static final double MAX_SIZE_PROPORTION_TO_WORLD = 0.05;
    private static final int MIN_POLLINATION_PERIOD = 10;
    private static final int MAX_POLLINATION_PERIOD = 40;

    @Override
    protected LivingAgent createAgent() {
        int id = getWorld().getNextId();
        MDC.put("agentId", String.valueOf(id));
        Mood startingMood = new Growing(id);

        int maxSizePossible = (int) (Math.min(getWorld().getWidth(), getWorld().getHeight()) * MAX_SIZE_PROPORTION_TO_WORLD);
        int plantMaxSize = RandomProvider.nextInt(LivingAgent.MIN_SIZE, Math.max(maxSizePossible, LivingAgent.MIN_SIZE + 1));
        double pollinationProbability = RandomProvider.nextDouble(MAX_POLLINATION_PROBABILITY);
        int pollinationPeriod = RandomProvider.nextInt(MIN_POLLINATION_PERIOD, MAX_POLLINATION_PERIOD);
        LivingAgent result = new Plant(id, getWorld().getRandomPosition(), startingMood, plantMaxSize, pollinationProbability, pollinationPeriod);
        LOG.info("Created {}", result);
        getWorld().addAgent(result);
        MDC.remove("agentId");

        return result;
    }

    @SuppressWarnings("ClassEscapesDefinedScope")
    public Agent createClone(Plant original) {
        int cloneId = getWorld().getNextId();
        Position clonePosition = getClonePosition(original);
        Agent result = new Plant(cloneId, clonePosition, new Growing(cloneId), original.getMaxSize(), original.getPollinationProbability(), original.getPollinationPeriod());
        getWorld().addAgent(result);

        return result;
    }

    private Position getClonePosition(Plant original) {
        int worldSize = Math.min(getWorld().getWidth(), getWorld().getHeight()); //assume the world is square
        int maxDispersion = (int) Math.round(worldSize * MAX_DISPERSION_WORLD_PROPORTION);
        int windDispersion = maxDispersion / original.getMaxSize(); //the bigger the plant, the smaller the distance
        int xDistanceFromOriginal = RandomProvider.nextInt(-windDispersion, windDispersion);
        int yDistanceFromOriginal = RandomProvider.nextInt(-windDispersion, windDispersion);

        Position position = new Position(original.position().getX() + xDistanceFromOriginal, original.position().getY() + yDistanceFromOriginal);
        return position.truncate(1, 1, getWorld().getWidth() - 2, getWorld().getHeight() - 2);
    }
}
