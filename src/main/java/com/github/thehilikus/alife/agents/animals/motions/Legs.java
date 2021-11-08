package com.github.thehilikus.alife.agents.animals.motions;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.Edge;
import com.github.thehilikus.alife.world.RandomProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.util.*;

/**
 * Motion that always moves forward until reaching the edge of the world
 */
public class Legs implements Locomotion {
    private static final Logger LOG = LoggerFactory.getLogger(Legs.class.getSimpleName());
    private static final int MOVE_AND_ROTATE_MAX = 45;
    private final int agentId;

    private final Position position;
    private int orientation;

    private final int topSpeed;
    @DecimalMin("-1.0")
    @DecimalMax("0.0")
    private final double energyExpenditureFactor;

    public Legs(int agentId, Position position, Genome genome) {
        this(agentId, position, RandomProvider.nextInt(Locomotion.FULL_TURN), genome);
    }

    Legs(int agentId, Position position, int orientation, Genome genome) {
        this.agentId = agentId;
        this.position = position;
        this.orientation = orientation;
        this.topSpeed = genome.getGene(Locomotion.PARAMETER_PREFIX + "topSpeed");
        this.energyExpenditureFactor = genome.getGene(Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor");
    }

    @Override
    public int move(double speedFactor, SortedSet<ScanResult> scanResults) {
        Optional<ScanResult> edgeOptional = findEdgeInCurrentOrientation(scanResults);
        int result;
        if (edgeOptional.isPresent()) {
            ScanResult scanResult = edgeOptional.get();
            int distanceToEdge = (int) Math.sqrt(scanResult.getDistanceSquared());
            result = moveTowardsTarget(speedFactor, distanceToEdge, scanResult.getRelativeDirection());
            if (result == distanceToEdge - 1) {
                //agent hit the edge, turn
                LOG.debug("Bouncing off edge");
                turnAfterEdgeCollision(scanResult.getAgent());
            }
        } else {
            result = moveForwards(speedFactor, Integer.MAX_VALUE);
        }

        return result;
    }

    private Optional<ScanResult> findEdgeInCurrentOrientation(Iterable<ScanResult> scanResults) {
        ScanResult smallestDirection = new ScanResult(1000, Locomotion.FULL_TURN, null);
        for (ScanResult scanResult : scanResults) {
            if (scanResult.getAgent() instanceof Edge) {
                if (Math.abs(scanResult.getRelativeDirection()) < Math.abs(smallestDirection.getRelativeDirection())) {
                    smallestDirection = scanResult;
                }
            }
        }

        Optional<ScanResult> result = Optional.empty();
        if (Math.abs(smallestDirection.getRelativeDirection()) < Locomotion.HALF_TURN / 4) {
            result = Optional.of(smallestDirection);
        }
        return result;
    }

    private void turnAfterEdgeCollision(Agent edge) {
        int edgeX = edge.getPosition().getX();
        int edgeY = edge.getPosition().getY();
        int positiveOrientation = orientation;
        if (orientation < 0) {
            positiveOrientation = Locomotion.FULL_TURN + orientation;
        }
        if (edgeX == 0 && edgeY != 0) {
            //left wall
            if (positiveOrientation < Locomotion.HALF_TURN) {
                turn(Locomotion.LEFT_TURN);
            } else if (positiveOrientation > Locomotion.HALF_TURN) {
                turn(Locomotion.RIGHT_TURN);
            } else {
                turn(Locomotion.HALF_TURN);
            }
        } else if (edgeY == 0 && edgeX != 0) {
            //top wall
            if (positiveOrientation < 270) {
                turn(Locomotion.LEFT_TURN);
            } else if (positiveOrientation > 270) {
                turn(Locomotion.RIGHT_TURN);
            } else {
                turn(Locomotion.HALF_TURN);
            }
        } else if (edgeX > position.getX() && edgeY != 0) {
            //right wall
            if (positiveOrientation < Locomotion.HALF_TURN) {
                turn(Locomotion.RIGHT_TURN);
            } else if (positiveOrientation > Locomotion.HALF_TURN) {
                turn(Locomotion.LEFT_TURN);
            } else {
                turn(Locomotion.HALF_TURN);
            }
        } else if (edgeY > position.getY() && edgeX != 0) {
            //bottom wall
            if (positiveOrientation < 90) {
                turn(Locomotion.LEFT_TURN);
            } else if (positiveOrientation > 90) {
                turn(Locomotion.RIGHT_TURN);
            } else {
                turn(Locomotion.HALF_TURN);
            }
        } else {
            //corners
            turn(Locomotion.HALF_TURN);
        }
    }

    @Override
    public int moveTowardsTarget(double speedFactor, int distance, int orientationOffset) {
        if (Math.abs(orientationOffset) > Locomotion.HALF_TURN) {
            throw new IllegalArgumentException("Orientation offset must be reduced to its smallest representation: e.g. 359 -> -1");
        }
        int movement = 0;
        if (distance > 1) { //since otherwise we are already next to the target
            if (Math.abs(orientationOffset) < MOVE_AND_ROTATE_MAX) {
                orientation = Math.floorMod(orientation + orientationOffset, Locomotion.FULL_TURN);
                movement = moveForwards(speedFactor, distance - 1);
            } else {
                //only rotate
                LOG.debug("Only adjusting angle to target by {}", orientationOffset);
                orientation = Math.floorMod(orientation + orientationOffset, Locomotion.FULL_TURN);
            }
        }
        return movement;
    }

    private int moveForwards(double speedFactor, int maxMovement) {
        int movementDelta = (int) Math.ceil(topSpeed * speedFactor);
        movementDelta = Math.min(movementDelta, maxMovement);
        if (movementDelta != maxMovement) {
            LOG.info("Walking {} spaces in direction {}°", movementDelta, orientation);
        } else {
            LOG.info("Walking only {} spaces in direction {}° because it is close to the target", movementDelta, orientation);
        }

        position.move(orientation, movementDelta);
        return movementDelta;
    }

    @Override
    public double getEnergyExpenditureFactor() {
        return energyExpenditureFactor;
    }

    @Override
    public Position.Immutable getPosition() {
        return position.toImmutable();
    }

    @Override
    public int getOrientation() {
        return orientation;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(PARAMETER_PREFIX + "topSpeed", topSpeed);
        result.put(PARAMETER_PREFIX + "energyExpenditureFactor", energyExpenditureFactor);
        result.put(PARAMETER_PREFIX + "orientation", orientation);

        return result;
    }

    @Override
    public void turn(int degrees) {
        orientation = Math.floorMod(orientation + degrees, Locomotion.FULL_TURN);
    }
}
