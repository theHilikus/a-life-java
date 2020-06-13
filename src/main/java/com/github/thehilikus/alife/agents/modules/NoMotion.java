package com.github.thehilikus.alife.agents.modules;

import com.github.thehilikus.alife.api.Coordinates;
import com.github.thehilikus.alife.api.Motion;
import com.github.thehilikus.alife.world.World;

import java.util.Map;

/**
 * A static motion
 */
public class NoMotion implements Motion {
    private final int agentId;
    private final Coordinates currentPosition;

    private NoMotion(int agentId, Coordinates currentPosition) {
        this.agentId = agentId;
        this.currentPosition = currentPosition;
    }

    public static Motion create(int id, World world) {
        return new NoMotion(id, world.getEmptyPosition());
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
    public int move(SpeedType speedType) {
        return 0;
    }

    @Override
    public int moveTo(Coordinates destination, SpeedType speedType) {
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
