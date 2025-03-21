package com.github.thehilikus.alife.agent.factories;

import com.github.thehilikus.alife.agent.api.LivingAgentFactory;
import com.github.thehilikus.alife.agent.api.LivingAgent;
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

    @Override
    protected LivingAgent createAgent() {
        int id = getWorld().getNextId();
        MDC.put("agentId", String.valueOf(id));
        Mood startingMood = new Growing(id);

        final double maxSizeProportionToWorld = 0.09;
        int maxSize = (int) (Math.min(getWorld().getWidth(), getWorld().getHeight()) * maxSizeProportionToWorld);
        LivingAgent result = new Plant(id, getWorld().getRandomPosition(), startingMood, maxSize);
        LOG.info("Created {}", result);
        getWorld().addAgent(result);
        MDC.remove("agentId");

        return result;
    }
}
