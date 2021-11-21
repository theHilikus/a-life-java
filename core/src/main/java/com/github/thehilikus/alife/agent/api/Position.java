package com.github.thehilikus.alife.agent.api;

import com.github.thehilikus.alife.agent.motion.api.PolarVector;

import javax.validation.constraints.PositiveOrZero;

/**
 * A mutable object to keep track of a position in the world
 */
public class Position {
    @PositiveOrZero
    private double x;
    @PositiveOrZero
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(PolarVector vector) {
        double speedX = Math.cos(Math.toRadians(vector.getAngle())) * vector.getMagnitude();
        double speedY = Math.sin(Math.toRadians(vector.getAngle())) * vector.getMagnitude();
        x += speedX;
        y += speedY;
    }

    public Immutable calculateMove(PolarVector vector) {
        double speedX = Math.cos(Math.toRadians(vector.getAngle())) * vector.getMagnitude();
        double speedY = Math.sin(Math.toRadians(vector.getAngle())) * vector.getMagnitude();

        return new Position(x + speedX, y + speedY).toImmutable();
    }

    public int getX() {
        return (int) Math.round(x);
    }

    public int getY() {
        return (int) Math.round(y);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public Immutable toImmutable() {
        return new Immutable(this);
    }

    /**
     * The immutable version of {@link Position}
     */
    public static class Immutable {
        private final int x;
        private final int y;

        private Immutable(Position mutable) {
            this.x = (int) Math.round(mutable.x);
            this.y = (int) Math.round(mutable.y);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "Immutable{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Immutable immutable = (Immutable) o;

            if (x != immutable.x) return false;
            return y == immutable.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }

        public boolean isNextTo(Immutable position) {
            int xDelta = Math.abs(x - position.x);
            int yDelta = Math.abs(y - position.y);

            return xDelta + yDelta == 1 || xDelta == 1 && yDelta == 1;
        }
    }
}
