package com.github.thehilikus.alife.agents.animals.motions;

import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Coordinates;
import com.github.thehilikus.alife.api.Locomotion;
import com.github.thehilikus.alife.api.Orientation;

import java.util.Map;

/**
 * Motion that moves towards a target
 */
public class TargetWalk implements Locomotion {
    private final Agent target;
    private final StraightWalk walker;

    public TargetWalk(Agent target, StraightWalk walker) {
        this.target = target;
        this.walker = walker;
    }

    @Override
    public int move(double speedFactor, Orientation direction) {
        Coordinates.Immutable destination = target.getPosition();
        Coordinates.Immutable position = walker.getPosition();
        int deltaX = destination.getX() - position.getX();
        int deltaY = destination.getY() - position.getY();
        Orientation orientation;
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            //move in X
            if (deltaX < 0) {
                orientation = Orientation.WEST;
            } else {
                orientation = Orientation.EAST;
            }
        } else {
            //move in Y
            if (deltaY < 0) {
                orientation = Orientation.NORTH;
            } else {
                orientation = Orientation.SOUTH;
            }
        }

        return walker.move(speedFactor, orientation);
    }

    @Override
    public double getEnergyExpenditureFactor() {
        return walker.getEnergyExpenditureFactor();
    }

    @Override
    public Coordinates.Immutable getPosition() {
        return walker.getPosition();
    }

    @Override
    public int getAgentId() {
        return walker.getAgentId();
    }

    @Override
    public Map<String, String> getParameters() {
        return walker.getParameters();
    }
}
