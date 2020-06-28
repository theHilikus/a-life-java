package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.animals.moods.*;
import com.github.thehilikus.alife.agents.animals.motions.Legs;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;

/**
 * Controls the transitions between moods
 */
public class HerbivoreMoodController implements MoodController {
    private final Vision vision;
    private final Legs legs;
    private final Locomotion locomotion;
    private final Genome genome;
    private final HungerTracker hungerTracker;
    private final EnergyTracker energyTracker;

    public HerbivoreMoodController(Vision vision, Legs legs, Locomotion locomotion, Genome genome, HungerTracker hungerTracker, EnergyTracker energyTracker) {
        this.vision = vision;
        this.legs = legs;
        this.locomotion = locomotion;
        this.genome = genome;
        this.hungerTracker = hungerTracker;
        this.energyTracker = energyTracker;
    }

    @Override
    public Mood startHunting(Agent.Eatable target) {
        return new Hunting(this, vision, legs, genome, target);
    }

    @Override
    public Mood startScouting() {
        return new Scouting(this, vision, locomotion, genome);
    }

    @Override
    public Mood startSleeping() {
        return new Sleeping(this, energyTracker);
    }

    @Override
    public Mood startEating(Agent.Eatable food) {
        return new Eating(this, vision, genome, food, hungerTracker);
    }

    @Override
    public Mood startIdling() {
        return new Existing(vision, genome, locomotion);
    }
}
