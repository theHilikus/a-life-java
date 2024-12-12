package com.github.thehilikus.alife.agent.motion;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.motion.api.CartesianVector;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.motion.api.PolarVector;
import com.github.thehilikus.alife.agent.vision.api.ScanResult;
import com.github.thehilikus.alife.world.Edge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.util.*;

/**
 * Motion that always moves forward until reaching the edge of the world
 */
public class Legs implements Locomotion {
    private static final Logger LOG = LoggerFactory.getLogger(Legs.class);
    private static final int MOVE_AND_ROTATE_MAX = 45;
    private final int agentId;

    private final int worldHeight;
    private final int worldWidth;
    private final Position position;
    private int orientation;
    private double cosOrientation;
    private double sinOrientation;

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
        turn(orientation);
        this.topSpeed = genome.getGene(Locomotion.PARAMETER_PREFIX + "topSpeed");
        this.energyExpenditureFactor = genome.getGene(Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor");
    }

    @Override
    public double move(double speedFactor, SortedSet<ScanResult> scanResults) {
        double maxMovement = Math.ceil(topSpeed * speedFactor);
        double endX = position.getX() + cosOrientation * maxMovement;
        double endY = position.getY() + sinOrientation * maxMovement;

        double result;

        final double edgeStartPosition = 0.5;
        final double edgeEndPosition = 1.5;
        if (endX < edgeStartPosition || endX > worldWidth - edgeEndPosition || endY < edgeStartPosition || endY > worldHeight - edgeEndPosition) {
            LOG.debug("Normal movement of {} would leave agent outside the world. endX={}, endY={}", maxMovement, endX, endY);
            result = moveToEdge(scanResults, maxMovement);
            turnAfterEdgeCollision();
        } else {
            result = moveForwards(speedFactor, Integer.MAX_VALUE);
        }

        return result;
    }

    private double moveToEdge(Collection<ScanResult> scanResults, double maxMovement) {
        if (scanResults.isEmpty()) {
            return 0;
        }

        double result = 0;
        for (int extrapolationDistance = 0; extrapolationDistance < maxMovement; extrapolationDistance++) {
            for (ScanResult scanResult : scanResults) {
                if (scanResult.getDistanceSquared() > maxMovement * maxMovement) {
                    //will never reach the remaining agents
                    break;
                }
                if (scanResult.getAgent() instanceof Edge) {
                    if (position.toImmutable().isNextTo(scanResult.getAgent().getPosition()) && isFacing(scanResult.getAgent())) {
                        return result;
                    }
                }
            }
            final int irrelevant = 1;
            result += moveForwards(irrelevant, 1);
        }

        throw new IllegalStateException("Never reached edge. currentPosition=" + position + ", orientation=" + orientation + ", scans=" + scanResults);
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
        CartesianVector wallNormal;
        if (position.getX() == 1) {
            //west wall
            wallNormal = new CartesianVector(1, 0);
        } else if (position.getY() == 1) {
            //north wall
            wallNormal = new CartesianVector(0, 1);
        } else if (position.getX() == worldWidth - 2) {
            //east wall
            wallNormal = new CartesianVector(-1, 0);
        } else if (position.getY() == worldHeight - 2) {
            //south wall
            wallNormal = new CartesianVector(0, -1);
        } else {
            throw new IllegalStateException("Did not hit a wall");
        }

        CartesianVector current = new PolarVector(orientation, 1).toCartesian();
        CartesianVector reflection = current.plus(wallNormal.multiply(-2 * current.dot(wallNormal)));
        double reflectionAngleInRadians = Math.atan2(reflection.getY(), reflection.getX());
        int reflectionAngle = (int) (Math.round(Math.toDegrees(reflectionAngleInRadians)) - orientation);
        LOG.debug("Bouncing off edge");
        turn(reflectionAngle);
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
            turn(vector.getAngle());
            movementEnergy = moveForwards(speedFactor, vector.getMagnitude());
        } else {
            //only rotate
            LOG.debug("Only adjusting angle to target by {}", vector.getAngle());
            turn(vector.getAngle());
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

    private double moveForwards(double speedFactor, double maxMovement) {
        double movementDelta = Math.ceil(topSpeed * speedFactor);
        movementDelta = Math.min(movementDelta, maxMovement);
        position.move(cosOrientation * movementDelta, sinOrientation * movementDelta);
        if (movementDelta != maxMovement) {
            LOG.info("Walking {} spaces in direction {}°", movementDelta, orientation);
        } else {
            LOG.info("Walking only {} spaces in direction {}° because it is close to the target. New position={}", movementDelta, orientation, position);
        }

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
        int originalOrientation = orientation;
        orientation = Math.floorMod(orientation + degrees, Turn.FULL);
        cosOrientation = Math.cos(Math.toRadians(orientation));
        sinOrientation = Math.sin(Math.toRadians(orientation));

        LOG.info("Turned from {}° to {}°", originalOrientation, orientation);
    }
}
