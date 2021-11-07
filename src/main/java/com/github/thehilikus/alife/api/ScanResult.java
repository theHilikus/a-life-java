package com.github.thehilikus.alife.api;

import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * Each agent found in a scan
 */
public class ScanResult implements Comparable<ScanResult> {
    @PositiveOrZero
    @NegativeOrZero
    private final int distanceSquared;

    @PositiveOrZero
    @NegativeOrZero
    private final int direction;

    @NotNull
    private final Agent agent;

    public ScanResult(int distanceSquared, int direction, Agent agent) {
        this.distanceSquared = distanceSquared;
        this.direction = direction;
        this.agent = agent;
    }

    @Override
    public int compareTo(ScanResult other) {
        return distanceSquared - other.distanceSquared;
    }

    public int getDistanceSquared() {
        return distanceSquared;
    }

    public int getRelativeDirection() {
        return direction;
    }

    public Agent getAgent() {
        return agent;
    }
}
