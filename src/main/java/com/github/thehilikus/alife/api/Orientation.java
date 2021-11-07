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

    public static final int FULL_TURN = 360;
    public static final int HALF_TURN = 180;
    public static final int RIGHT_TURN = 90;
    public static final int LEFT_TURN = -RIGHT_TURN;
    private final int index;

    Orientation(int index) {
        this.index = index;
    }

    public double toRadians() {
        return Math.toRadians(toDegrees());
    }

    @SuppressWarnings("MagicNumber")
    public int toDegrees() {
        int result;
        switch (this) {
            case EAST:
                result = 0;
                break;
            case NORTH:
                result = 270;
                break;
            case WEST:
                result = 180;
                break;
            case SOUTH:
                result = 90;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }

        return result;
    }
}
