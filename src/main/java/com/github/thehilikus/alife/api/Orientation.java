package com.github.thehilikus.alife.api;

/**
 * One of the four directions
 */
@SuppressWarnings("JavaDoc")
public enum Orientation {
    NORTH(0),
    WEST(1),
    SOUTH(2),
    EAST(3);

    private final int index;

    Orientation(int index) {
        this.index = index;
    }

    public static Orientation fromInt(int index) {
        return Orientation.values()[index];
    }

    public Orientation turn(Orientation direction) {
        int delta = direction == WEST ? 1 : -1;

        return Orientation.fromInt(Math.floorMod(index + delta, Orientation.values().length));
    }

    public Orientation opposite() {
        return turn(WEST).turn(WEST);
    }

    public double toRadians() {
        double result = 0;
        switch (this) {
            case WEST:
                result = 3.0 / 2 * Math.PI;
                break;
            case SOUTH:
                result = Math.PI;
                break;
            case EAST:
                result = Math.PI / 2;
                break;
        }

        return result;
    }
}
