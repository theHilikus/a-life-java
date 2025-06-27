package com.github.thehilikus.alife.agent.moods;

import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.plants.Plant;

import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

/**
 * A plant that is recovering energy
 */
public class Growing implements Mood {
    private final int agentId;
    private final int frozenTime;

    private static final int RECOVERY_RATE = 1;
    private int tickCounter;

    public Growing(int agentId) {
        this(agentId, 10);
    }

    public Growing(int agentId, int frozenTime) {
        this.agentId = agentId;
        this.frozenTime = frozenTime;
    }

    @Override
    public Mood tick(LivingAgent me) {
        Plant mePlant = (Plant) me;
        if (mePlant.isFullSize()) {
            if (tickCounter > frozenTime) {
                return new Pollinating(agentId, mePlant.getPollinationProbability(), ((Plant) me).getPollinationPeriod());
            } else {
                tickCounter++;
            }
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
        return Map.of(
                PARAMETER_PREFIX + "frozenTime", frozenTime,
                PARAMETER_PREFIX + "freezeCounter",  tickCounter
        );

    }

    @Override
    public String toString() {
        return "Growing{}";
    }
}
