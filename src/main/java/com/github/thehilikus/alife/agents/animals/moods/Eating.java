package com.github.thehilikus.alife.agents.animals.moods;

import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.controllers.HungerTracker;
import com.github.thehilikus.alife.agents.controllers.SizeTracker;
import com.github.thehilikus.alife.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

/**
 * An agent converting food energy into hunger points
 */
public class Eating implements Mood {
    private static final Logger LOG = LoggerFactory.getLogger(Eating.class.getSimpleName());
    private static final double SIZE_TO_BITE_RATIO = 0.75;
    private static final double EATING_FATIGUE_FACTOR = 1.5;
    private static final int PRIORITY = 85;

    private final MoodController moodController;
    private final Vision vision;
    private final Agent.Eatable food;
    private final HungerTracker hungerTracker;
    private final SizeTracker sizeTracker;
    private int lastBite;

    public Eating(MoodController moodController, Vision vision, SizeTracker sizeTracker, Agent.Eatable foodAgent, HungerTracker hungerTracker) {
        this.moodController = moodController;
        this.vision = vision;
        this.food = foodAgent;
        this.hungerTracker = hungerTracker;
        this.sizeTracker = sizeTracker;
    }

    @Override
    public Mood tick() {
        SortedSet<ScanResult> scanResult = vision.scan(food.getClass()::isInstance);
        Optional<ScanResult> targetOptional = scanResult.stream().filter(scan -> scan.getAgent().getId() == this.food.getId()).findFirst();
        if (targetOptional.isPresent()) {
            if (hungerTracker.isFull()) {
                LOG.debug("Agent {} is full", getAgentId());
                return moodController.startIdling();
            }
            int eatSpeed = (int) Math.max(1, Math.round(sizeTracker.getValue() * SIZE_TO_BITE_RATIO));
            int biteSize = Math.min(eatSpeed, HungerTracker.FULL_THRESHOLD - hungerTracker.getValue());
            LOG.trace("Biting agent {} with bite size = {}. Current hunger = {}", food.getId(), biteSize, hungerTracker.getValue());
            lastBite = food.transferEnergy(biteSize);
        } else {
            LOG.debug("Food {} is finished", food.getId());
            return moodController.startIdling();
        }

        return this;
    }

    @Override
    public int getHungerDelta() {
        return lastBite;
    }

    @Override
    public int getEnergyDelta() {
        return (int) Math.round(EnergyTracker.ENERGY_DERIVATIVE * EATING_FATIGUE_FACTOR);
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public int getAgentId() {
        return vision.getAgentId();
    }

    @Override
    public Map<String, Object> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "lastBite", lastBite
        );
    }

    @Override
    public String toString() {
        return "Eating{" +
                "food=" + food +
                '}';
    }
}
