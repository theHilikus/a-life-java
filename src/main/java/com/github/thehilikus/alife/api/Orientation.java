package com.github.thehilikus.alife.api;

/**
 * One of the four directions
 */
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

        return Orientation.fromInt((index + delta) % Orientation.values().length);
    }
}
