package com.github.thehilikus.alife.api;

/**
 * A strategy to move in the world
 */
public interface Motion extends Component {
    /**
     * The prefix for motion parameters
     */
    String PARAMETER_PREFIX = "motion.";

    int move(SpeedType speedType);

    int moveTo(Coordinates destination, SpeedType speedType);

    double getEnergyExpenditureFactor();

    Coordinates.Immutable getPosition();

    enum SpeedType {
        SCOUT,
        HUNT,
        IDLE,
        ESCAPE
    }
}
