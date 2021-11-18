package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.animals.moods.AgentModules;
import com.github.thehilikus.alife.agents.animals.moods.InHeat;
import com.github.thehilikus.alife.agents.animals.moods.Scouting;
import com.github.thehilikus.alife.agents.animals.moods.Sleeping;
import com.github.thehilikus.alife.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Aggregates the information of all the agent vitals
 */
public class VitalsController implements Component {
    private static final Logger LOG = LoggerFactory.getLogger(VitalsController.class.getSimpleName());
    private final HungerTracker hungerTracker;
    private final EnergyTracker energyTracker;
    private final AgeTracker ageTracker;
    private final ReproductionTracker reproductionTracker;
    private final SizeTracker sizeTracker;
    private final AgentModules dependencies;
    private final int agentId;

    public VitalsController(int agentId, AgentModules dependencies) {
        this.agentId = agentId;
        this.dependencies = dependencies;
        this.hungerTracker = dependencies.getHungerTracker();
        this.energyTracker = dependencies.getEnergyTracker();
        this.ageTracker = dependencies.getAgeTracker();
        this.reproductionTracker = dependencies.getReproductionTracker();
        this.sizeTracker = dependencies.getSizeTracker();
    }

    public void updateTrackers(Mood currentMood) {
        hungerTracker.update(currentMood);
        energyTracker.update(currentMood);
        ageTracker.update();
        reproductionTracker.update(currentMood);
        sizeTracker.update();
    }

    public boolean isAlive() {
        return hungerTracker.isAlive() && energyTracker.isAlive() && ageTracker.isAlive();
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, Object> getDetails() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.putAll(hungerTracker.getDetails());
        result.putAll(energyTracker.getDetails());
        result.putAll(ageTracker.getDetails());
        result.putAll(reproductionTracker.getDetails());
        result.putAll(sizeTracker.getDetails());

        return result;
    }

    public VitalSign getCauseOfDeath() {
        VitalSign result = null;
        if (!hungerTracker.isAlive()) {
            result = hungerTracker;
        } else if (!energyTracker.isAlive()) {
            result = energyTracker;
        } else if (!ageTracker.isAlive()) {
            result = ageTracker;
        }

        return result;
    }

    public Mood nextMood(Mood currentMood) {
        Mood result = currentMood;
        if (energyTracker.isTired()) {
            LOG.debug("{} is tired", agentId);
            result = new Sleeping(dependencies);
        }
        if (hungerTracker.isHungry()) {
            Mood scouting = new Scouting(dependencies);
            if (scouting.getPriority() > result.getPriority()) {
                LOG.debug("{} is hungry", agentId);
                result = scouting;
            }
        }
        if (ageTracker.isTeenAge() && reproductionTracker.isWombRested()) {
            Mood inHeat = new InHeat(dependencies);
            if (inHeat.getPriority() > result.getPriority()) {
                LOG.debug("{} is in heat", agentId);
                result = inHeat;
            }
        }

        return result;
    }
}
