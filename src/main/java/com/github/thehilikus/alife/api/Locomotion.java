package com.github.thehilikus.alife.api;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.SortedSet;

/**
 * An un-targeted strategy to move in the world
 */
public interface Locomotion extends Component {
    /**
     * The prefix for motion parameters
     */
    String PARAMETER_PREFIX = "motion.";

    /**
     * Move the agent without a specific purpose
     *
     * @param speedFactor the proportion of the max velocity to use as speed
     * @param scanResults the result of the latest scan
     * @return the distance travelled
     */
    @PositiveOrZero
    int move(double speedFactor, SortedSet<ScanResult> scanResults);

    /**
     * Move the agent towards a specific destination
     *
     * @param speedFactor     the proportion of the max velocity to use as speed
     * @param targetDistance  the target distance relative to its own
     * @param targetDirection the target direction relative to its own
     * @return the distance travelled
     */
    @PositiveOrZero
    int moveTowardsTarget(double speedFactor, int targetDistance, int targetDirection);

    @DecimalMin("-1.0")
    @DecimalMax("0.0")
    double getEnergyExpenditureFactor();

    @NotNull
    Position.Immutable getPosition();

    int getOrientation();

    void turn(int degrees);
}
