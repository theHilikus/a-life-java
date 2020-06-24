package com.github.thehilikus.alife.api;

/**
 * A strategy to move in the world
 */
public interface Locomotion extends Component {
    /**
     * The prefix for motion parameters
     */
    String PARAMETER_PREFIX = "motion.";

    default int move(double speedFactor) {
        return move(speedFactor, Integer.MAX_VALUE);
    }

    int move(double speedFactor, int maxMovement);

    double getEnergyExpenditureFactor();

    Position.Immutable getPosition();
}
