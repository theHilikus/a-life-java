package com.github.thehilikus.alife.agents.plants;

import com.github.thehilikus.alife.api.Coordinates;
import com.github.thehilikus.alife.api.Locomotion;
import com.github.thehilikus.alife.api.Orientation;
import com.github.thehilikus.alife.world.World;

import java.util.Map;

/**
 * A static motion
 */
public class NoLocomotion implements Locomotion {
    private final int agentId;
    private final Coordinates currentPosition;

    public NoLocomotion(int agentId, World world) {
        this.agentId = agentId;
        this.currentPosition = world.getEmptyPosition();
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(PARAMETER_PREFIX + "position", currentPosition.getX() + ", " + currentPosition.getY());
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
    public Coordinates.Immutable getPosition() {
        return new Coordinates.Immutable(currentPosition);
    }
}
