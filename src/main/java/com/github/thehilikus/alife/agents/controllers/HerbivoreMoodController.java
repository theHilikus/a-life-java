package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.animals.moods.Hunting;
import com.github.thehilikus.alife.agents.animals.moods.Scouting;
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

    public HerbivoreMoodController(Vision vision, Legs legs, Locomotion locomotion, Genome genome) {
        this.vision = vision;
        this.legs = legs;
        this.locomotion = locomotion;
        this.genome = genome;
    }

    @Override
    public Mood startHunting(Agent.Living target) {
        return new Hunting(this, vision, legs, genome, target);
    }

    @Override
    public Mood startScouting() {
        return new Scouting(this, vision, locomotion, genome);
    }

    @Override
    public Mood startSleeping() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public Mood startEating() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public Mood startIdling() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }
}
