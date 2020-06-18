package com.github.thehilikus.alife.api;

/**
 * Each agent found in a scan
 */
public class ScanResult implements Comparable<ScanResult> {
    private final int xDistance;
    private final int yDistance;
    private final Agent agent;
    private final boolean isEdge;

    public ScanResult(int xDistance, int yDistance) {
        this(xDistance, yDistance, null, true);
    }

    public ScanResult(int xDistance, int yDistance, Agent agent) {
        this(xDistance, yDistance, agent, false);
    }

    private ScanResult(int xDistance, int yDistance, Agent agent, boolean isEdge) {
        if (xDistance < 0 || yDistance < 0) {
            throw new IllegalArgumentException("Distance has to be an absolute value");
        }
        this.xDistance = xDistance;
        this.yDistance = yDistance;
        this.agent = agent;
        this.isEdge = isEdge;
    }

    @Override
    public int compareTo(ScanResult other) {
        int distanceSquared = (xDistance << 1) + (yDistance << 1);
        int otherDistanceSquared = (other.xDistance << 1) + (other.yDistance << 1);

        return distanceSquared - otherDistanceSquared;
    }


    public int getXDistance() {
        return xDistance;
    }

    public int getYDistance() {
        return yDistance;
    }

    public boolean isEdge() {
        return isEdge;
    }

    public Agent getAgent() {
        return agent;
    }
}
