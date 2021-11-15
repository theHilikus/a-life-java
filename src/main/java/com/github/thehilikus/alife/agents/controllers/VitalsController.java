package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.animals.moods.AgentModules;
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

    public Mood update(Mood lastMood, Mood newMood) {
        hungerTracker.update(lastMood);
        energyTracker.update(lastMood);
        ageTracker.update();
        reproductionTracker.update();
        sizeTracker.update();

        Mood result = newMood;
        if (energyTracker.isTired()) {
            LOG.debug("{} is tired", agentId);
            if (newMood.getPriority() < Sleeping.PRIORITY) {
                result = moodController.startSleeping();
            }
        }
        if (hungerTracker.isHungry()) {
            LOG.debug("{} is hungry", agentId);
            if (newMood.getPriority() < Scouting.PRIORITY) {
                result = moodController.startScouting();
            }
        }

        return result;
    }

    public boolean isAlive() {
        return hungerTracker.isAlive() && energyTracker.isAlive() && ageTracker.isAlive();
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.putAll(hungerTracker.getParameters());
        result.putAll(energyTracker.getParameters());
        result.putAll(ageTracker.getParameters());
        result.putAll(reproductionTracker.getParameters());
        result.putAll(sizeTracker.getParameters());

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

    public void gaveBirth(int fatherId, Agent offspring) {
        reproductionTracker.gaveBirth(fatherId, offspring.getId());
    }
}
