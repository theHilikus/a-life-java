package com.github.thehilikus.alife.agents.animals.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.controllers.HungerTracker;
import com.github.thehilikus.alife.agents.genetics.Genome;
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
    private static final double SIZE_TO_BITE_RATIO = 0.1;
    private static final double EATING_FATIGUE_FACTOR = 1.5;

    private final MoodController moodController;
    private final int agentId;
    private final Vision vision;
    private final Agent.Eatable food;
    private final int eatSpeed;
    private final HungerTracker hungerTracker;
    private int lastBite;

    public Eating(MoodController moodController, Vision vision, Genome genome, Agent.Eatable foodAgent, HungerTracker hungerTracker) {
        this.moodController = moodController;
        this.agentId = vision.getAgentId();
        this.vision = vision;
        this.food = foodAgent;
        this.hungerTracker = hungerTracker;
        this.eatSpeed = (int) Math.max(1, Math.round((int) genome.getGene("size") * SIZE_TO_BITE_RATIO));
    }

    @Override
    public Mood tick() {
        SortedSet<ScanResult> scanResult = vision.scan(food.getClass());
        Optional<ScanResult> targetOptional = scanResult.stream().filter(scan -> scan.getAgent().getId() == this.food.getId()).findFirst();
        if (targetOptional.isPresent()) {
            LOG.trace("Biting {}", food);
            lastBite = food.transferEnergy(eatSpeed);
            if (hungerTracker.isFull()) {
                LOG.debug("Agent {} is full", agentId);
                return moodController.startIdling();
            }
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
    public Ansi.FColor getTerminalColour() {
        return Ansi.FColor.GREEN;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "current", getClass().getSimpleName(),
                PARAMETER_PREFIX + "eatSpeed", Integer.toString(eatSpeed),
                PARAMETER_PREFIX + "lastBite", Integer.toString(lastBite)
        );
    }

    @Override
    public String toString() {
        return "Eating{" +
                "food=" + food +
                '}';
    }
}
