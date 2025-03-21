package com.github.thehilikus.alife.agent.moods;

import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.moods.api.Mood;

import java.util.Map;

/**
 * An Eatable agent being eaten
 */
public class BeingEaten implements Mood {
    private final int agentId;
    private int hoursSinceLastBite;
    private int bittenInHour;

    public BeingEaten(int agentId) {
        this.agentId = agentId;
    }

    public void bite(int biteSize) {
        bittenInHour += biteSize;
        hoursSinceLastBite = 0;
    }

    @Override
    public Mood tick(LivingAgent me) {
        hoursSinceLastBite++;

        Mood result = this;
        if (hoursSinceLastBite > 2) {
            result = new Growing(agentId);
        }
        return result;
    }

    @Override
    public int getHungerDelta() {
        return 0;
    }

    @Override
    public int getEnergyDelta() {
        int result = bittenInHour * -1;
        bittenInHour = 0;

        return result;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of(
                PARAMETER_PREFIX + "hoursSinceLastBite", hoursSinceLastBite,
                PARAMETER_PREFIX + "bittenInHour", bittenInHour
        );
    }

    @Override
    public String toString() {
        return "BeingEaten{" +
                "hoursSinceLastBite=" + hoursSinceLastBite +
                ", bittenInHour=" + bittenInHour +
                '}';
    }
}
