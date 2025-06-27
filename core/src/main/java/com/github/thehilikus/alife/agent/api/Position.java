package com.github.thehilikus.alife.agent.api;

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

    public void move(double velocityX, double velocityY) {
        x += velocityX;
        y += velocityY;
    }

    public int getX() {
        return (int) Math.round(x);
    }

    public int getY() {
        return (int) Math.round(y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public Immutable toImmutable() {
        return new Immutable(this);
    }

    /**
     * Returns a new position which is the same as this one, but with the x and y coordinates
     * clamped to the given range.
     *
     * @param xMin the minimum x value
     * @param yMin the minimum y value
     * @param xMax the maximum x value
     * @param yMax the maximum y value
     * @return a new position with x and y coordinates clamped to the given range
     */
    public Position truncate(int xMin, int yMin, int xMax, int yMax) {
        double newX = Math.max(Math.min(x, xMax), xMin);
        double newY = Math.max(Math.min(y, yMax), yMin);
        return new Position(newX, newY);
    }

    /**
     * The immutable version of {@link Position}
     */
    public static class Immutable {
        private final int x;
        private final int y;

        private Immutable(Position mutable) {
            this(mutable.x, mutable.y);
        }

        public Immutable(double x, double y) {
            this.x = (int) Math.round(x);
            this.y = (int) Math.round(y);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
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
