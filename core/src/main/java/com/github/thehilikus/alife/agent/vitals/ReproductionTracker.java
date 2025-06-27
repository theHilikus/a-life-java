package com.github.thehilikus.alife.agent.vitals;

import com.github.thehilikus.alife.agent.vitals.api.VitalSign;

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
    public void update(int unused) {
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
        offsprings.add(offspringId);
        mates.add(otherParentId);
        timeSinceReproduction = 0;
    }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of(
                PARAMETER_PREFIX + "timeSinceReproduction", timeSinceReproduction,
                PARAMETER_PREFIX + "mates", mates,
                PARAMETER_PREFIX + "offspring", offsprings
        );
    }

    public boolean isWombRested() {
        return timeSinceReproduction > POST_REPRODUCTION_WAIT;
    }
}
