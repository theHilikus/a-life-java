package com.github.thehilikus.alife.agents.plants;

import com.github.thehilikus.alife.agents.LivingAgentFactory;
import com.github.thehilikus.alife.agents.plants.moods.Growing;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.world.IdsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds {@link Plant}
 */
public class PlantFactory extends LivingAgentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PlantFactory.class);

    @Override
    protected Agent.Living createAgent() {
        int id = IdsProvider.getNextId();
        Mood startingMood = new Growing(id);

        final double maxSizeProportionToWorld = 0.09;
        int maxSize = (int) (Math.min(getWorld().getWidth(), getWorld().getHeight()) * maxSizeProportionToWorld);
        Agent.Living result = new Plant(id, getWorld().getRandomPosition(), startingMood, maxSize);
        LOG.info("Created {}", result);
        getWorld().addAgent(result);

        return result;
    }
}
