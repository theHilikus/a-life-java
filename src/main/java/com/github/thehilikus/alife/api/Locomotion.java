package com.github.thehilikus.alife.api;

/**
 * A strategy to move in the world
 */
public interface Locomotion extends Component {
    /**
     * The prefix for motion parameters
     */
    String PARAMETER_PREFIX = "motion.";

    default int move(double speedFactor, Orientation direction) {
        throw new UnsupportedOperationException("Invalid operation on this type of locomotion");
    }
    default int move(double speedFactor) {
        return move(speedFactor, null);
    }

    double getEnergyExpenditureFactor();

    Position.Immutable getPosition();
}
