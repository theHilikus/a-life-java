package com.github.thehilikus.alife.agents.animals.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.controllers.HungerTracker;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.MoodController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * An agent recovering energy
 */
public class Sleeping implements Mood {
    private static final Logger LOG = LoggerFactory.getLogger(Sleeping.class.getSimpleName());
    private static final double HUNGER_SLOWDOWN_FACTOR = 0.5;
    private static final int ENERGY_RECOVERY = 10;
    private final MoodController moodController;
    private final int agentId;
    private final EnergyTracker energyTracker;

    public Sleeping(MoodController moodController, EnergyTracker energyTracker) {
        this.moodController = moodController;
        this.agentId = energyTracker.getAgentId();
        this.energyTracker = energyTracker;
    }

    @Override
    public @NotNull Mood tick() {
        if (energyTracker.isRested()) {
            LOG.debug("Agent {} is rested", agentId);
            return moodController.startIdling();
        }

        return this;
    }

    @Override
    public int getHungerDelta() {
        return (int) Math.round(HungerTracker.HUNGER_DERIVATIVE * HUNGER_SLOWDOWN_FACTOR);
    }

    @Override
    public int getEnergyDelta() {
        return ENERGY_RECOVERY;
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
                PARAMETER_PREFIX + "current", getClass().getSimpleName(),
                PARAMETER_PREFIX + "energyRecovery", Integer.toString(ENERGY_RECOVERY),
                PARAMETER_PREFIX + "hungerSlowdownFactor", Double.toString(HUNGER_SLOWDOWN_FACTOR)
        );
    }

    @Override
    public String toString() {
        return "Sleeping{}";
    }
}
