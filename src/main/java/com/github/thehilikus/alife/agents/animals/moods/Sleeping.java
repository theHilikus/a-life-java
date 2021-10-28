package com.github.thehilikus.alife.agents.animals.moods;

import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
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
    public static final int PRIORITY = 90;
    private static final Logger LOG = LoggerFactory.getLogger(Sleeping.class.getSimpleName());
    private static final double HUNGER_SLOWDOWN_FACTOR = 0.5;
    private static final int ENERGY_RECOVERY_RATE = 10;
    private final MoodController moodController;
    private final EnergyTracker energyTracker;

    public Sleeping(MoodController moodController, EnergyTracker energyTracker) {
        this.moodController = moodController;
        this.energyTracker = energyTracker;
    }

    @Override
    public @NotNull Mood tick() {
        if (energyTracker.isRested()) {
            LOG.debug("Agent {} is rested", getAgentId());
            return moodController.startIdling();
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
    public Map<String, Object> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "energyRecovery", ENERGY_RECOVERY_RATE,
                PARAMETER_PREFIX + "hungerSlowdownFactor", HUNGER_SLOWDOWN_FACTOR
        );
    }

    @Override
    public String toString() {
        return "Sleeping{}";
    }
}
