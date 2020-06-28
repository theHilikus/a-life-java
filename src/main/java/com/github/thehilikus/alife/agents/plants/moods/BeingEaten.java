package com.github.thehilikus.alife.agents.plants.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.Mood;

import java.util.Map;

/**
 * A Eatable agent being eaten
 */
public class BeingEaten implements Mood {
    private final int agentId;
    private int daysSinceLastBite;
    private int bittenToday;

    public BeingEaten(int agentId) {
        this.agentId = agentId;
    }

    public void bite(int biteSize) {
        bittenToday += biteSize;
        daysSinceLastBite = 0;
    }

    @Override
    public Mood tick() {
        daysSinceLastBite++;

        Mood result = this;
        if (daysSinceLastBite > 2) {
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
        int result = bittenToday * -1;
        bittenToday = 0;

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
                PARAMETER_PREFIX + "daysSinceLastBite", Integer.toString(daysSinceLastBite),
                PARAMETER_PREFIX + "bittenToday", Integer.toString(bittenToday)
        );
    }

    @Override
    public String toString() {
        return "BeingEaten{" +
                "daysSinceLastBite=" + daysSinceLastBite +
                ", bittenToday=" + bittenToday +
                '}';
    }
}
