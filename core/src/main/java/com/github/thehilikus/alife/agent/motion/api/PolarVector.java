package com.github.thehilikus.alife.agent.motion.api;

import com.github.thehilikus.alife.agent.api.Position;

/**
 * A vector with angle and magnitude
 */
public class PolarVector {
    private final int angle;
    private final double magnitude;

    public PolarVector(Position.Immutable origin, int originOrientation, Position.Immutable target) {
        int xDelta = target.getX() - origin.getX();
        int yDelta = target.getY() - origin.getY();
        double targetAngleInRadians = Math.atan2(yDelta, xDelta);
        int relativeAngle = (int) Math.round(Math.toDegrees(targetAngleInRadians) - originOrientation) % Locomotion.Turn.FULL;
        if (relativeAngle < -Locomotion.Turn.HALF) {
            //represent it in the other direction to make it smaller than 180
            relativeAngle = (relativeAngle + Locomotion.Turn.FULL) % Locomotion.Turn.FULL;
        }
        assert Math.abs(relativeAngle) <= Locomotion.Turn.HALF : "Relative angle must be > -180 and < 180 but was " + relativeAngle;

        angle = relativeAngle;
        magnitude = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
    }

    public PolarVector(int angle, double magnitude) {
        this.angle = angle;
        this.magnitude = magnitude;
    }

    public int getAngle() {
        return angle;
    }

    public double getMagnitude() {
        return magnitude;
    }
}
