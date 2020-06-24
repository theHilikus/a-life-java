package com.github.thehilikus.alife.api;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * A strategy to move in the world
 */
public interface Locomotion extends Component {
    /**
     * The prefix for motion parameters
     */
    String PARAMETER_PREFIX = "motion.";

    @PositiveOrZero
    default int move(double speedFactor) {
        return move(speedFactor, Integer.MAX_VALUE);
    }

    @PositiveOrZero
    int move(double speedFactor, int maxMovement);

    @DecimalMin("-1.0")
    @DecimalMax("0.0")
    double getEnergyExpenditureFactor();

    @NotNull
    Position.Immutable getPosition();
}
