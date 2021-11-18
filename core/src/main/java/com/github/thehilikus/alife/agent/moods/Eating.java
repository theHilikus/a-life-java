package com.github.thehilikus.alife.agent.moods;

import com.github.thehilikus.alife.agent.api.internal.EatableAgent;
import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.vision.api.ScanResult;
import com.github.thehilikus.alife.agent.vision.api.Vision;
import com.github.thehilikus.alife.agent.vitals.AgentModules;
import com.github.thehilikus.alife.agent.vitals.EnergyTracker;
import com.github.thehilikus.alife.agent.vitals.HungerTracker;
import com.github.thehilikus.alife.agent.vitals.SizeTracker;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
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

    private final Vision vision;
    private final EatableAgent food;
    private final HungerTracker hungerTracker;
    private final SizeTracker sizeTracker;
    private final AgentModules dependencies;
    private int lastBite;

    public Eating(AgentModules dependencies, EatableAgent foodAgent) {
        this.food = foodAgent;
        this.dependencies = dependencies;
        this.vision = dependencies.getVision();
        this.hungerTracker = dependencies.getHungerTracker();
        this.sizeTracker = dependencies.getSizeTracker();
    }

    @Override
    public Mood tick(LivingAgent me) {
        SortedSet<ScanResult> scanResult = vision.scan(food.getClass()::isInstance);
        Optional<ScanResult> targetOptional = scanResult.stream().filter(scan -> scan.getAgent().getId() == this.food.getId()).findFirst();
        if (targetOptional.isPresent()) {
            int eatSpeed = (int) Math.max(1, Math.round(sizeTracker.getValue() * SIZE_TO_BITE_RATIO));
            int biteSize = Math.min(eatSpeed, VitalSign.FULL_THRESHOLD - hungerTracker.getValue());
            LOG.trace("Biting agent {} with bite size = {}. Current hunger = {}", food.getId(), biteSize, hungerTracker.getValue());
            lastBite = food.transferEnergy(biteSize);
            if (hungerTracker.isFullAfter(lastBite)) {
                LOG.debug("Agent {} is full", getAgentId());
                return new Existing(dependencies);
            }
        } else {
            LOG.debug("Food {} is finished", food.getId());
            return new Existing(dependencies);
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
    public Map<String, Object> getDetails() {
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
