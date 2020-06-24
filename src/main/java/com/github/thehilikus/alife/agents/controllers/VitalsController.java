package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Component;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Aggregates the information of all the agent vitals
 */
public class VitalsController implements Component {
    private static final Logger LOG = LoggerFactory.getLogger(VitalsController.class);
    private final HungerTracker hungerTracker;
    private final EnergyTracker energyTracker;
    private final AgeTracker ageTracker;
    private final int agentId;
    private final MoodController moodController;

    public VitalsController(int agentId, MoodController moodController, Genome genome) {
        this.agentId = agentId;
        this.hungerTracker = new HungerTracker(agentId, genome);
        this.moodController = moodController;
        this.energyTracker = new EnergyTracker(agentId, genome);
        this.ageTracker = new AgeTracker(agentId, genome.getGene(VitalSign.PARAMETER_PREFIX + "lifeExpectancy"));
    }

    public Mood update(Mood lastMood) {
        hungerTracker.update(lastMood);
        energyTracker.update(lastMood);
        ageTracker.update();
        //TODO: find proper mood

        return lastMood;
    }

    public boolean isAlive() {
        return hungerTracker.isAlive() || energyTracker.isAlive() || ageTracker.isAlive();
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
