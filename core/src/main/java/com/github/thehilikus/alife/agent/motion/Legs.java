package com.github.thehilikus.alife.agent.motion;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.motion.api.PolarVector;
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

    private final int worldHeight;
    private final Position position;
    private final int worldWidth;
    private int orientation;

    private final int topSpeed;
    @DecimalMin("-1.0")
    @DecimalMax("0.0")
    private final double energyExpenditureFactor;

    protected Legs(int worldWidth, int worldHeight, int agentId, Position position, Genome genome) {
        this(worldWidth, worldHeight, agentId, position, RandomProvider.nextInt(Turn.FULL), genome);
    }

    Legs(int worldWidth, int worldHeight, int agentId, Position position, int orientation, Genome genome) {
        this.agentId = agentId;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.position = position;
        this.orientation = orientation;
        this.topSpeed = genome.getGene(Locomotion.PARAMETER_PREFIX + "topSpeed");
        this.energyExpenditureFactor = genome.getGene(Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor");
    }

    @Override
    public double move(double speedFactor, SortedSet<ScanResult> scanResults) {
        Optional<ScanResult> edgeOptional = findEdgeInCurrentOrientation(scanResults);
        double result = 0;
        if (edgeOptional.isPresent()) {
            double maxMovement = Math.ceil(topSpeed * speedFactor);
            PolarVector maxMovementVector = new PolarVector(orientation, maxMovement);
            Position.Immutable endPosition = position.calculateMove(maxMovementVector);
            if (endPosition.getX() < 1 || endPosition.getX() > worldWidth - 2 || endPosition.getY() < 1 || endPosition.getY() > worldHeight - 2) {
                //walk 1 by 1 since full movement goes outside
                for (int accumulatedMovement = 0; accumulatedMovement < maxMovement; accumulatedMovement++) {
                    if (position.getX() > 1 && position.getY() > 1 && position.getX() < worldWidth - 2 && position.getY() < worldHeight - 2) {
                        result += moveForwards(speedFactor, 1);
                    }
                    if (position.getX() == 1 || position.getY() == 1 || position.getX() == worldWidth - 2 || position.getY() == worldHeight - 2) {
                        turnAfterEdgeCollision();
                        break;
                    }
                }
            } else {
                result = moveForwards(speedFactor, Integer.MAX_VALUE);
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

    private void turnAfterEdgeCollision() {
        LOG.debug("Bouncing off edge");
        int positiveOrientation = orientation;
        if (orientation < 0) {
            positiveOrientation = Turn.FULL + orientation;
        }
        if (position.getX() == 1) {
            //west wall
            if (positiveOrientation < Turn.HALF) {
                turn(Turn.LEFT);
            } else if (positiveOrientation > Turn.HALF) {
                turn(Turn.RIGHT);
            } else {
                turn(Turn.HALF);
            }
        } else if (position.getY() == 1) {
            //north wall
            if (positiveOrientation < Orientation.NORTH) {
                turn(Turn.LEFT);
            } else if (positiveOrientation > Orientation.NORTH) {
                turn(Turn.RIGHT);
            } else {
                turn(Turn.HALF);
            }
        } else if (position.getX() == worldWidth - 2) {
            //east wall
            if (positiveOrientation < Turn.HALF) {
                turn(Turn.RIGHT);
            } else if (positiveOrientation > Turn.HALF) {
                turn(Turn.LEFT);
            } else {
                turn(Turn.HALF);
            }
        } else if (position.getY() == worldHeight - 2) {
            //south wall
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
    public double moveTowardsTarget(double speedFactor, Position.Immutable target) {
        double movementEnergy = 0;
        if (!position.toImmutable().isNextTo(target)) {
            PolarVector closestPositionNextToTarget = findClosestPositionNextTo(target);
            movementEnergy = moveTo(speedFactor, closestPositionNextToTarget);
        }

        return movementEnergy;
    }

    private double moveTo(double speedFactor, PolarVector vector) {
        if (Math.abs(vector.getAngle()) > Turn.HALF) {
            throw new IllegalArgumentException("Orientation offset must be reduced to its smallest representation: e.g. 359 -> -1");
        }

        double movementEnergy = 0;
        if (Math.abs(vector.getAngle()) < MOVE_AND_ROTATE_MAX) {
            adjustAngle(vector.getAngle());
            movementEnergy = moveForwards(speedFactor, vector.getMagnitude());
        } else {
            //only rotate
            LOG.debug("Only adjusting angle to target by {}", vector.getAngle());
            adjustAngle(vector.getAngle());
        }

        return movementEnergy;
    }

    private PolarVector findClosestPositionNextTo(Position.Immutable target) {
        PolarVector result = new PolarVector(0, Integer.MAX_VALUE);
        Position.Immutable current = position.toImmutable();
        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                Position.Immutable nextToTarget = new Position(target.getX() + col, target.getY() + row).toImmutable();
                PolarVector vector = new PolarVector(current, orientation, nextToTarget);
                if (vector.getMagnitude() < result.getMagnitude()) {
                    result = vector;
                }
            }
        }

        return result;
    }

    private void adjustAngle(int orientationOffset) {
        orientation = Math.floorMod(orientation + orientationOffset, Turn.FULL);
    }

    private double moveForwards(double speedFactor, double maxMovement) {
        double movementDelta = Math.ceil(topSpeed * speedFactor);
        movementDelta = Math.min(movementDelta, maxMovement);
        if (movementDelta != maxMovement) {
            LOG.info("Walking {} spaces in direction {}°", movementDelta, orientation);
        } else {
            LOG.info("Walking only {} spaces in direction {}° because it is close to the target", movementDelta, orientation);
        }
        position.move(new PolarVector(orientation, movementDelta));

        return movementDelta * energyExpenditureFactor;
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
