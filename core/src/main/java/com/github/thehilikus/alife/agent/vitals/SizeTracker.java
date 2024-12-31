package com.github.thehilikus.alife.agent.vitals;

import com.github.thehilikus.alife.agent.vitals.api.VitalSign;

import java.util.Map;

/**
 * Keeps track of the current size of the agent
 */
public class SizeTracker implements VitalSign {
    private static final int SLOWING_FACTOR = 3;
    private final int maxSize;
    private int currentSize;
    private int updates;

    public SizeTracker(int initialSize, int maxSize) {
        this.maxSize = maxSize;
        this.currentSize = initialSize;
    }

    @Override
    public void update(int unused) {
        updates++;
        if (updates % SLOWING_FACTOR == 0) {
            currentSize = Math.min(currentSize + 1, maxSize);
        }
    }

    @Override
    public boolean isAlive() {
        return currentSize > 0;
    }

    @Override
    public int getValue() {
        return currentSize;
    }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of("size", currentSize,
                PARAMETER_PREFIX + "maxSize", maxSize);
    }

    public boolean isFullSize() {
        return currentSize >= maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
