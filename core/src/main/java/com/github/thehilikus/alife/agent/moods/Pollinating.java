package com.github.thehilikus.alife.agent.moods;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.factories.PlantFactory;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.plants.Plant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * When the plant can reproduce by spreading its seeds
 */
public class Pollinating implements Mood {
    private static final Logger LOG = LoggerFactory.getLogger(Pollinating.class);
    private static final int POLLINATING_ENERGY_SPENT = -40;
    private static final int PRIORITY = 80;

    private final int agentId;
    private final double pollinationProbability;
    private boolean pollinated;

    public Pollinating(int agentId, double pollinationProbability) {
        this.agentId = agentId;
        this.pollinationProbability = pollinationProbability;
    }

    @Override
    public Mood tick(LivingAgent me) {
        if (RandomProvider.nextDouble(1) < pollinationProbability) {
            LOG.debug("Plant {} is pollinating", agentId);
            Agent clone = new PlantFactory().createClone((Plant) me);
            LOG.info("Created clone of plant {}. Clone: {}", me.getId(), clone);
            pollinated = true;
            return new Growing(agentId);
        } else {
            pollinated = false;
        }

        return this;
    }

    @Override
    public int getEnergyDelta() {
        int result = 0;
        if (pollinated) {
            result = POLLINATING_ENERGY_SPENT;
        }

        return result;
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of(
                PARAMETER_PREFIX + "pollinationProbability", pollinationProbability
        );
    }

    @Override
    public String toString() {
        return "Pollinating{" +
                "probability=" + String.format("%.2f", pollinationProbability) +
                '}';
    }
}
