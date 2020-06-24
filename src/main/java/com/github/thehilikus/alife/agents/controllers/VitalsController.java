package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

/**
 * Aggregates the information of all the agent vitals
 */
public class VitalsController {
    private final HungerTracker hungerTracker;
    private final EnergyTracker energyTracker;
    private final AgeTracker ageTracker;

    public VitalsController(Genome genome) {
        this.hungerTracker = new HungerTracker();
        this.energyTracker = new EnergyTracker();
        this.ageTracker = new AgeTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "lifeExpectancy"));
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
}
