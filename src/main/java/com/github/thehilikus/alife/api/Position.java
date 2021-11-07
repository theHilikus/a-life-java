package com.github.thehilikus.alife.api;

import javax.validation.constraints.PositiveOrZero;

/**
 * A mutable object to keep track of a position in the world
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

    public void move(int direction, int speed) {
        int speedX = (int) Math.round(Math.cos(Math.toRadians(direction)) * speed);
        int speedY = (int) Math.round(Math.sin(Math.toRadians(direction)) * speed);
        x += speedX;
        y += speedY;
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
