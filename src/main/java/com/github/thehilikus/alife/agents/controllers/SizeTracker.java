package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

import java.util.Map;

/**
 * Keeps track of the current size of the agent
 */
public class SizeTracker implements VitalSign {
    private static final int SLOWING_FACTOR = 3;
    private final int maxSize;
    private int size = Agent.MIN_SIZE;
    private int updates;

    public SizeTracker(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void update(Mood currentMood) {
        updates++;
        if (updates % SLOWING_FACTOR == 0) {
            size = Math.min(size + 1, maxSize);

        }
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public int getValue() {
        return size;
    }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("size", size,
                PARAMETER_PREFIX + "maxSize", maxSize);
    }
}
