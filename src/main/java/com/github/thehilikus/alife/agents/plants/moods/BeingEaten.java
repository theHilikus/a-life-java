package com.github.thehilikus.alife.agents.plants.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.Mood;

import java.util.Map;

/**
 * A Eatable agent being eaten
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
    public Mood tick() {
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
    public Ansi.FColor getTerminalColour() {
        return Ansi.FColor.YELLOW;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "hoursSinceLastBite", Integer.toString(hoursSinceLastBite),
                PARAMETER_PREFIX + "bittenInHour", Integer.toString(bittenInHour)
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
