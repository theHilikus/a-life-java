package com.github.thehilikus.alife.agents.controllers;

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
    private int timeSinceReproduction;
    private final Collection<Integer> offspring = new ArrayList<>();
    private final Collection<Integer> mates = new ArrayList<>();

    @Override
    public void update(Mood currentMood) {
        timeSinceReproduction++;
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
        offspring.add(offspringId);
        mates.add(otherParentId);
        timeSinceReproduction = 0;
    }

    @Override
    public Map<String, Object> getParameters() {
        return Map.of(
                Agent.Evolvable.PARAMETER_PREFIX + "timeSinceReproduction", timeSinceReproduction,
                Agent.Evolvable.PARAMETER_PREFIX + "mates", mates,
                Agent.Evolvable.PARAMETER_PREFIX + "offspring", offspring
        );
    }
}
