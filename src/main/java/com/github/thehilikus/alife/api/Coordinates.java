package com.github.thehilikus.alife.api;

/**
 * An mutable object to keep track of a position in the world
 */
public class Coordinates {
    private int x;
    private int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates move(Orientation direction, int speed) {
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
        return new Coordinates(newX, newY);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * The immutable version of {@link Coordinates}
     */
    public static class Immutable {
        private final int x;
        private final int y;

        public Immutable(Coordinates mutable) {
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
    }
}
