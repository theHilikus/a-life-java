package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.animals.moods.Eating;
import com.github.thehilikus.alife.agents.animals.moods.Hunting;
import com.github.thehilikus.alife.agents.animals.moods.Scouting;
import com.github.thehilikus.alife.api.Component;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.MoodController;
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
    private final int agentId;
    private final MoodController moodController;

    public VitalsController(int agentId, MoodController moodController, HungerTracker hungerTracker, EnergyTracker energyTracker, AgeTracker ageTracker) {
        this.agentId = agentId;
        this.hungerTracker = hungerTracker;
        this.moodController = moodController;
        this.energyTracker = energyTracker;
        this.ageTracker = ageTracker;
    }

    public Mood update(Mood lastMood, Mood newMood) {
        hungerTracker.update(lastMood);
        energyTracker.update(lastMood);
        ageTracker.update();

        Mood result = newMood;
        if (energyTracker.isTired()) {
            LOG.debug("{} is tired", agentId);
            result = moodController.startSleeping();
        }
        if (hungerTracker.isHungry() && !(newMood instanceof Hunting || newMood instanceof Scouting || newMood instanceof Eating)) {
            LOG.debug("{} is hungry", agentId);
            result = moodController.startScouting();
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
    public Map<String, String> getParameters() {
        Map<String, String> result = new LinkedHashMap<>();
        result.putAll(hungerTracker.getParameters());
        result.putAll(energyTracker.getParameters());
        result.putAll(ageTracker.getParameters());

        return result;
    }
}
