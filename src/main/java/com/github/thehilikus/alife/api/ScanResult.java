package com.github.thehilikus.alife.api;

/**
 * Each agent found in a scan
 */
public class ScanResult implements Comparable<ScanResult> {
    private final int xDistance;
    private final int yDistance;
    private final Agent agent;

    public ScanResult(int xDistance, int yDistance, Agent agent) {
        this.xDistance = xDistance;
        this.yDistance = yDistance;
        this.agent = agent;
    }

    @Override
    public int compareTo(ScanResult other) {
        int distanceSquared = (int) (Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
        int otherDistanceSquared = (other.xDistance << 1) + (other.yDistance << 1);

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
