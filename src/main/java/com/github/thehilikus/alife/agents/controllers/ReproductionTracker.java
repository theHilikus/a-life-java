package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.animals.moods.Mating;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Records information about an agent's pregnancy
 */
public class ReproductionTracker implements VitalSign {
    private static final int POST_REPRODUCTION_WAIT = 10;
    private int timeSinceReproduction;
    private final Collection<Integer> offsprings = new ArrayList<>();
    private final Collection<Integer> mates = new ArrayList<>();

    @Override
    public void update(Mood currentMood) {
        if (currentMood.getClass() != Mating.class) {
            timeSinceReproduction++;
        }
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public int getValue() {
        return timeSinceReproduction;
    }

    public void gaveBirth(int otherParentId, int offspringId) {
        offsprings.add(offspringId);
        mates.add(otherParentId);
        timeSinceReproduction = 0;
    }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of(
                Agent.Evolvable.PARAMETER_PREFIX + "timeSinceReproduction", timeSinceReproduction,
                Agent.Evolvable.PARAMETER_PREFIX + "mates", mates,
                Agent.Evolvable.PARAMETER_PREFIX + "offspring", offsprings
        );
    }

    public boolean isWombRested() {
        return timeSinceReproduction > POST_REPRODUCTION_WAIT;
    }
}
