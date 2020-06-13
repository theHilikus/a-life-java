package com.github.thehilikus.alife.api;

/**
 * Each agent found in a scan
 */
public class ScanResult implements Comparable<ScanResult> {
    private final int distanceSquared;
    private final Agent agent;

    public ScanResult(int distanceSquared, Agent agent) {
        this.distanceSquared = distanceSquared;
        this.agent = agent;
    }

    @Override
    public int compareTo(ScanResult other) {
        return distanceSquared - other.distanceSquared;
    }

    public double getDistanceSquared() {
        return distanceSquared;
    }

    public Agent getAgent() {
        return agent;
    }
}
