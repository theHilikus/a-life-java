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
     * Move the agent towards a specific distination
     *
     * @param speedFactor the proportion of the max velocity to use as speed
     * @param target      the final location to reach
     * @return the distance travelled
     */
    @PositiveOrZero
    int moveTowardsTarget(double speedFactor, Position.Immutable target);

    @DecimalMin("-1.0")
    @DecimalMax("0.0")
    double getEnergyExpenditureFactor();

    @NotNull
    Position.Immutable getPosition();

    void faceTowards(Position.Immutable position);
}
