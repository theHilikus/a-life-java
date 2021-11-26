package com.github.thehilikus.alife.agent.motion.api;

/**
 * A vector with in cartesian coordinates
 */
public class CartesianVector {
    private final double x;
    private final double y;

    public CartesianVector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double dot(CartesianVector other) {
        return x * other.x + y * other.y;
    }

    public CartesianVector multiply(double scalar) {
        return new CartesianVector(x * scalar, y * scalar);
    }

    public CartesianVector plus(CartesianVector other) {
        return new CartesianVector(x + other.x, y + other.y);
    }

    public CartesianVector subtract(CartesianVector other) {
        return new CartesianVector(x - other.x, y - other.y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public PolarVector toPolar() {
        double angle = Math.atan2(y, x);
        double magnitude = getMagnitude();
        return new PolarVector((int) Math.round(Math.toDegrees(angle)), magnitude);
    }

    @Override
    public String toString() {
        return "CartesianVector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public CartesianVector normalize() {
        double magnitude = getMagnitude();

        double x = this.x;
        double y = this.y;
        if (magnitude != 0) {
            x /= magnitude;
            y /= magnitude;
        }

        return new CartesianVector(x, y);
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public CartesianVector divide(double divisor) {
        return new CartesianVector(x / divisor, y / divisor);
    }
}
