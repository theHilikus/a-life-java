package com.github.thehilikus.alife.agents.plants;

import com.github.thehilikus.alife.api.Locomotion;
import com.github.thehilikus.alife.api.Orientation;
import com.github.thehilikus.alife.api.Position;

import java.util.Collections;
import java.util.Map;

/**
 * A static motion
 */
public class NoLocomotion implements Locomotion {
    private final int agentId;
    private final Position position;

    public NoLocomotion(int agentId, Position position) {
        this.agentId = agentId;
        this.position = position;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Collections.emptyMap();
    }

    @Override
    public int move(double speedFactor, Orientation direction) {
        return 0;
    }

    @Override
    public double getEnergyExpenditureFactor() {
        return 0;
    }

    @Override
    public Position.Immutable getPosition() {
        return new Position.Immutable(position);
    }
}
