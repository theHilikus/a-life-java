package com.github.thehilikus.alife.agent.motion.api;

/**
 * A vector with in cartesian coordinates
 */
public record CartesianVector(double x, double y) {

    public double dot(CartesianVector other) {
        return x * other.x + y * other.y;
    }

    public CartesianVector multiply(double scalar) {
        return new CartesianVector(x * scalar, y * scalar);
    }

    public CartesianVector plus(CartesianVector other) {
        return new CartesianVector(x + other.x, y + other.y);
    }

    public PolarVector toPolar() {
        double angle = Math.atan2(y, x);
        double magnitude = Math.sqrt(x * x + y * y);
        return new PolarVector((int) Math.round(Math.toDegrees(angle)), magnitude);
    }

    @Override
    public String toString() {
        return "CartesianVector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
