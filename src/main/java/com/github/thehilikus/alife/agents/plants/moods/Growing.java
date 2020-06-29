package com.github.thehilikus.alife.agents.plants.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.Mood;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

/**
 * A plant that is recovering energy
 */
public class Growing implements Mood {
    private final int agentId;

    private static final int RECOVERY_RATE = 10;

    public Growing(int agentId) {
        this.agentId = agentId;
    }

    @Override
    public Mood tick() {
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
    public Ansi.FColor getTerminalColour() {
        return Ansi.FColor.MAGENTA;
    }

    @Override
    public @PositiveOrZero int getAgentId() {
        return agentId;
    }

    @Override
    public @NotNull Map<String, String> getParameters() {
        return Map.of(PARAMETER_PREFIX + "recoveryRate", Integer.toString(RECOVERY_RATE));
    }

    @Override
    public String toString() {
        return "Growing{}";
    }
}
