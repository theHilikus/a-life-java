package com.github.thehilikus.alife.api;

/**
 * A strategy to move in the world
 */
public interface Motion extends Component {
    int move(SpeedType speedType);

    int moveTo(Coordinates destination, SpeedType speedType);

    double getEnergyExpenditureFactor();

    Coordinates.Immutable getPosition();

    /**
     * The prefix for motion parameters
     */
    String PARAMETER_PREFIX = "motion.";

    enum SpeedType {
        SCOUT,
        HUNT,
        IDLE,
        ESCAPE
    }
}
