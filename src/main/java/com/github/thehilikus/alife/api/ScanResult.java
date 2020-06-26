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
    private final int xDistance;

    @PositiveOrZero
    @NegativeOrZero
    private final int yDistance;

    @NotNull
    private final Agent agent;

    public ScanResult(int xDistance, int yDistance, Agent agent) {
        this.xDistance = xDistance;
        this.yDistance = yDistance;
        this.agent = agent;
    }

    @Override
    public int compareTo(ScanResult other) {
        int distanceSquared = (int) (Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
        int otherDistanceSquared = (int) (Math.pow(other.xDistance, 2) + Math.pow(other.yDistance, 2));

        return distanceSquared - otherDistanceSquared;
    }

    public int getXDistance() {
        return xDistance;
    }

    public int getYDistance() {
        return yDistance;
    }

    public Agent getAgent() {
        return agent;
    }
}
