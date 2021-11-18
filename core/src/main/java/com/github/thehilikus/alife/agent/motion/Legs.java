package com.github.thehilikus.alife.agent.motion;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.vision.api.ScanResult;
import com.github.thehilikus.alife.world.Edge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

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

    protected Legs(int agentId, Position position, Genome genome) {
        this(agentId, position, RandomProvider.nextInt(Turn.FULL), genome);
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
        ScanResult smallestDirection = new ScanResult(1000, Turn.FULL, null);
        for (ScanResult scanResult : scanResults) {
            if (scanResult.getAgent() instanceof Edge) {
                int positiveRelativeDirection = Math.abs(scanResult.getRelativeDirection());
                int positiveSmallestFound = Math.abs(smallestDirection.getRelativeDirection());
                if (positiveRelativeDirection < positiveSmallestFound && isFacing(scanResult.getAgent())) {
                    smallestDirection = scanResult;
                }
            }
        }

        Optional<ScanResult> result = Optional.empty();
        if (smallestDirection.getAgent() != null) { //if we found at least one
            result = Optional.of(smallestDirection);
        }
        return result;
    }

    private boolean isFacing(Agent edge) {
        boolean facingWest = orientation > Orientation.SOUTH && orientation < Orientation.NORTH;
        boolean facingNorth = orientation > Turn.HALF;
        boolean facingEast = orientation > Orientation.NORTH || orientation < Orientation.SOUTH;
        boolean facingSouth = orientation < Turn.HALF;

        int deltaX = edge.getPosition().getX() - position.getX();
        int deltaY = edge.getPosition().getY() - position.getY();

        return deltaX < 0 && deltaY < 0 && facingWest && facingNorth
                || deltaX < 0 && deltaY > 0 && facingWest && facingSouth
                || deltaX > 0 && deltaY < 0 && facingEast && facingNorth
                || deltaX > 0 && deltaY > 0 && facingEast && facingSouth
                || deltaX < 0 && deltaY == 0 && facingWest
                || deltaX > 0 && deltaY == 0 && facingEast
                || deltaX == 0 && deltaY < 0 && facingNorth
                || deltaX == 0 && deltaY > 0 && facingSouth;
    }

    private void turnAfterEdgeCollision(Agent edge) {
        int edgeX = edge.getPosition().getX();
        int edgeY = edge.getPosition().getY();
        int positiveOrientation = orientation;
        if (orientation < 0) {
            positiveOrientation = Turn.FULL + orientation;
        }
        if (edgeX == 0 && edgeY != 0) {
            //left wall
            if (positiveOrientation < Turn.HALF) {
                turn(Turn.LEFT);
            } else if (positiveOrientation > Turn.HALF) {
                turn(Turn.RIGHT);
            } else {
                turn(Turn.HALF);
            }
        } else if (edgeY == 0 && edgeX != 0) {
            //top wall
            if (positiveOrientation < Orientation.NORTH) {
                turn(Turn.LEFT);
            } else if (positiveOrientation > Orientation.NORTH) {
                turn(Turn.RIGHT);
            } else {
                turn(Turn.HALF);
            }
        } else if (edgeX > position.getX() && edgeY != 0) {
            //right wall
            if (positiveOrientation < Turn.HALF) {
                turn(Turn.RIGHT);
            } else if (positiveOrientation > Turn.HALF) {
                turn(Turn.LEFT);
            } else {
                turn(Turn.HALF);
            }
        } else if (edgeY > position.getY() && edgeX != 0) {
            //bottom wall
            if (positiveOrientation < Turn.HALF / 2) {
                turn(Turn.LEFT);
            } else if (positiveOrientation > Turn.HALF / 2) {
                turn(Turn.RIGHT);
            } else {
                turn(Turn.HALF);
            }
        } else {
            //corners
            turn(Turn.HALF);
        }
    }

    @Override
    public int moveTowardsTarget(double speedFactor, int distance, int orientationOffset) {
        if (Math.abs(orientationOffset) > Turn.HALF) {
            throw new IllegalArgumentException("Orientation offset must be reduced to its smallest representation: e.g. 359 -> -1");
        }
        int movement = 0;
        if (distance > 1) { //since otherwise we are already next to the target
            if (Math.abs(orientationOffset) < MOVE_AND_ROTATE_MAX) {
                orientation = Math.floorMod(orientation + orientationOffset, Turn.FULL);
                movement = moveForwards(speedFactor, distance - 1);
            } else {
                //only rotate
                LOG.debug("Only adjusting angle to target by {}", orientationOffset);
                orientation = Math.floorMod(orientation + orientationOffset, Turn.FULL);
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
    public Map<String, Object> getDetails() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(PARAMETER_PREFIX + "topSpeed", topSpeed);
        result.put(PARAMETER_PREFIX + "orientation", orientation);

        return result;
    }

    @Override
    public void turn(int degrees) {
        orientation = Math.floorMod(orientation + degrees, Turn.FULL);
    }
}
