package com.github.thehilikus.alife.agent.moods;

import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.plants.Plant;

import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.Map;

/**
 * A plant that is recovering energy
 */
public class Growing implements Mood {
    private final int agentId;

    private static final int RECOVERY_RATE = 1;

    public Growing(int agentId) {
        this.agentId = agentId;
    }

    @Override
    public Mood tick(LivingAgent me) {
        Plant mePlant = (Plant) me;
        if (mePlant.isFullSize()) {
            return new Pollinating(agentId, mePlant.getPollinationProbability());
        }
        return this;
    }

    @Override
    public int getHungerDelta() {
        return 0;
    }

    @Override
    public int getEnergyDelta() {
        return RECOVERY_RATE;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public @PositiveOrZero int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, Object> getDetails() {
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return "Growing{}";
    }
}
