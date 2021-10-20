package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Records information about an agent's pregnancy
 */
public class ReproductionTracker implements VitalSign {
    private int timeSinceReproduction;
    private final Collection<String> encounters = new ArrayList<>();

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
        encounters.add("With parent " + otherParentId + " had offspring " + offspringId);
        timeSinceReproduction = 0;
    }
}
