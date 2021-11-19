package com.github.thehilikus.alife.agent.motion.api;

import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.api.internal.Component;
import com.github.thehilikus.alife.agent.vision.api.ScanResult;

import javax.validation.constraints.NotNull;
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
     * @return the energy spent in the last movement
     */
    double move(double speedFactor, SortedSet<ScanResult> scanResults);

    /**
     * Move the agent towards a specific destination
     *
     * @param speedFactor     the proportion of the max velocity to use as speed
     * @param targetDistance  the target distance relative to its own
     * @param targetDirection the target direction relative to its own
     * @return the energy spent in the last movement
     */
    double moveTowardsTarget(double speedFactor, int targetDistance, int targetDirection);

    @NotNull
    Position.Immutable getPosition();

    int getOrientation();

    void turn(int degrees);

    @SuppressWarnings("JavaDoc")
    interface Orientation {
        int EAST = 0;
        int SOUTH = 90;
        int WEST = 180;
        int NORTH = 270;
    }

    interface Turn {
        /**
         * A complete rotation
         */
        int FULL = 360;
        /**
         * Half a rotation
         */
        int HALF = 180;
        /**
         * A 90-degree turn to the right
         */
        int RIGHT = 90;
        /**
         * A 90-degree turn to the left
         */
        int LEFT = -RIGHT;
    }
}
