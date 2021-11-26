package com.github.thehilikus.alife.agent.factories;

import com.github.thehilikus.alife.agent.api.internal.IdsProvider;
import com.github.thehilikus.alife.agent.api.LivingAgentFactory;
import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.moods.Growing;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.plants.Plant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds {@link Plant}
 */
public class PlantFactory extends LivingAgentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PlantFactory.class.getSimpleName());

    @Override
    protected LivingAgent createAgent() {
        int id = IdsProvider.getNextId();
        Mood startingMood = new Growing(id);

        final double maxSizeProportionToWorld = 0.09;
        int maxSize = (int) (Math.min(getWorld().getWidth(), getWorld().getHeight()) * maxSizeProportionToWorld);
        LivingAgent result = new Plant(id, getWorld().getRandomPosition(), startingMood, maxSize);
        LOG.info("Created {}", result);
        getWorld().addAgent(result);

        return result;
    }
}
