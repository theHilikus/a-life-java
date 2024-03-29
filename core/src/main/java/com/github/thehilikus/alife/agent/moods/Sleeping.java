package com.github.thehilikus.alife.agent.moods;

import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.vitals.AgentModules;
import com.github.thehilikus.alife.agent.vitals.EnergyTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

/**
 * An agent recovering energy
 */
public class Sleeping implements Mood {
    /**
     * The priority of the mood from 1-100
     */
    private static final int PRIORITY = 90;
    private static final Logger LOG = LoggerFactory.getLogger(Sleeping.class);
    private static final double HUNGER_SLOWDOWN_FACTOR = 0.5;
    private static final int ENERGY_RECOVERY_RATE = 10;
    private final EnergyTracker energyTracker;
    private final AgentModules dependencies;

    public Sleeping(AgentModules dependencies) {
        this.dependencies = dependencies;
        this.energyTracker = dependencies.getEnergyTracker();
    }

    @Override
    public @NotNull Mood tick(LivingAgent me) {
        if (energyTracker.isRestedAfter(ENERGY_RECOVERY_RATE)) {
            LOG.debug("Agent {} is rested", getAgentId());
            return new Existing(dependencies);
        }

        return this;
    }

    @Override
    public int getHungerDelta() {
        return (int) Math.round(HUNGER_DERIVATIVE * HUNGER_SLOWDOWN_FACTOR);
    }

    @Override
    public int getEnergyDelta() {
        return ENERGY_RECOVERY_RATE;
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public int getAgentId() {
        return energyTracker.getAgentId();
    }

    @Override
    public Map<String, Object> getDetails() {
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return "Sleeping{}";
    }
}
