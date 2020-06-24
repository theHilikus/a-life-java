package com.github.thehilikus.alife.api;

import javax.validation.constraints.PositiveOrZero;

/**
 * An mutable object to keep track of a position in the world
 */
public class Position {
    @PositiveOrZero
    private int x;
    @PositiveOrZero
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position move(Orientation direction, int speed) {
        int newX = x;
        int newY = y;
        switch (direction) {
            case NORTH:
                y -= speed;
                break;
            case WEST:
                x -= speed;
                break;
            case SOUTH:
                y += speed;
                break;
            case EAST:
                x += speed;
        }
        return new Position(newX, newY);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

        public Immutable(Position mutable) {
            this.x = mutable.x;
            this.y = mutable.y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        /**
         * Finds the farthest direction to the destination
         *
         * @param destination the position to get to
         */
        public Orientation directionTo(Position.Immutable destination) {
            int deltaX = destination.x - this.x;
            int deltaY = destination.y - this.y;
            Orientation result;
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                //move in X
                if (deltaX < 0) {
                    result = Orientation.WEST;
                } else {
                    result = Orientation.EAST;
                }
            } else {
                //move in Y
                if (deltaY < 0) {
                    result = Orientation.NORTH;
                } else {
                    result = Orientation.SOUTH;
                }
            }

            return result;
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
    }
}
