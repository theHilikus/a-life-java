package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.animals.moods.*;
import com.github.thehilikus.alife.agents.animals.motions.Legs;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.World;

/**
 * Controls the transitions between moods
 */
public class HerbivoreMoodController implements MoodController {
    private static final int POST_REPRODUCTION_WAIT = 10;
    private final Vision vision;
    private final Legs legs;
    private final Locomotion locomotion;
    private final Genome genome;
    private final HungerTracker hungerTracker;
    private final EnergyTracker energyTracker;
    private final AgeTracker ageTracker;
    private final ReproductionTracker reproductionTracker;
    private final SizeTracker sizeTracker;
    private final World world;

    public HerbivoreMoodController(Vision vision, Legs legs, Locomotion locomotion, Genome genome, HungerTracker hungerTracker, EnergyTracker energyTracker, AgeTracker ageTracker, ReproductionTracker reproductionTracker, SizeTracker sizeTracker, World world) {
        this.vision = vision;
        this.legs = legs;
        this.locomotion = locomotion;
        this.genome = genome;
        this.hungerTracker = hungerTracker;
        this.energyTracker = energyTracker;
        this.ageTracker = ageTracker;
        this.reproductionTracker = reproductionTracker;
        this.sizeTracker = sizeTracker;
        this.world = world;
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
        return new Eating(this, vision, sizeTracker, food, hungerTracker);
    }

    @Override
    public Mood startIdling() {
        int teenAge = genome.getGene(Agent.Evolvable.PARAMETER_PREFIX + "teenAge");
        if (ageTracker.getValue() >= teenAge && reproductionTracker.getValue() > POST_REPRODUCTION_WAIT) {
            return new InHeat(this, vision, genome, locomotion);
        } else {
            return new Existing(vision, genome, locomotion);
        }
    }

    @Override
    public Mood startFollowing(Agent.Evolvable mate) {
        return new InHeatChasing(this, vision, legs, genome, mate);
    }

    @Override
    public Mood startMating(Agent.Evolvable mate) {
        return new Mating(this, vision, genome, reproductionTracker, mate, world);
    }
}
