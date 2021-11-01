package com.github.thehilikus.alife.agents.animals.motions;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.RandomProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.util.*;
import java.util.function.Predicate;

/**
 * Motion that always moves forward until reaching the edge of the world
 */
public class Legs implements Locomotion {
    private static final Logger LOG = LoggerFactory.getLogger(Legs.class);
    private final int agentId;

    private Position position;
    private Orientation orientation;

    private final int topSpeed;
    @DecimalMin("-1.0")
    @DecimalMax("0.0")
    private final double energyExpenditureFactor;

    public Legs(int agentId, Position position, Genome genome) {
        this.agentId = agentId;
        this.topSpeed = genome.getGene(Locomotion.PARAMETER_PREFIX + "topSpeed");
        this.energyExpenditureFactor = genome.getGene(Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor");
        this.position = position;
        orientation = Orientation.fromInt(RandomProvider.nextInt(4));
    }

    @Override
    public int move(double speedFactor, SortedSet<ScanResult> scanResults) {
        Optional<ScanResult> edgeOptional = findEdgeInCurrentOrientation(scanResults);
        if (edgeOptional.isPresent()) {
            ScanResult closestEdge = edgeOptional.get();
            if (isFacingEdge(closestEdge)) {
                orientation = orientation.opposite();

                return 0;
            }

        }
        return edgeOptional.map(scanResult -> moveTowardsTarget(speedFactor, scanResult.getAgent().getPosition())).orElseGet(() -> moveForwards(speedFactor, Integer.MAX_VALUE));
    }

    private boolean isFacingEdge(ScanResult closestEdge) {
        return closestEdge.getXDistance() == 1 && orientation == Orientation.EAST
                || closestEdge.getXDistance() == -1 && orientation == Orientation.WEST
                || closestEdge.getYDistance() == 1 && orientation == Orientation.SOUTH
                || closestEdge.getYDistance() == -1 && orientation == Orientation.NORTH;
    }

    private Optional<ScanResult> findEdgeInCurrentOrientation(Collection<ScanResult> scanResults) {
        Predicate<? super ScanResult> filter = null;

        switch (orientation) {
            case NORTH:
                filter = agent -> agent.getXDistance() == 0 && agent.getYDistance() < position.getY();
                break;
            case EAST:
                filter = agent -> agent.getYDistance() == 0 && agent.getXDistance() > position.getX();
                break;
            case SOUTH:
                filter = agent -> agent.getXDistance() == 0 && agent.getYDistance() > position.getY();
                break;
            case WEST:
                filter = agent -> agent.getYDistance() == 0 && agent.getXDistance() < position.getX();
                break;
        }

        return scanResults.stream().filter(filter).findFirst();
    }

    private int moveForwards(double speedFactor, int maxMovement) {
        int movementDelta = (int) Math.ceil(topSpeed * speedFactor);
        movementDelta = Math.min(movementDelta, maxMovement);
        if (movementDelta != maxMovement) {
            LOG.info("Walking {} {} spaces", orientation, movementDelta);
        } else {
            LOG.info("Walking {} only {} spaces because it is close to the target", orientation, movementDelta);
        }

        position.move(orientation, movementDelta);
        return movementDelta;
    }

    @Override
    public int moveTowardsTarget(double speedFactor, Position.Immutable target) {
        Orientation targetDirection = position.directionTo(target);
        int xDistance = position.getX() - target.getX();
        int yDistance = position.getY() - target.getY();
        int maxMovement = Math.max(Math.abs(xDistance) - 1, Math.abs(yDistance) - 1);

        int movement = 0;
        if (targetDirection != orientation) {
            orientation = targetDirection;
        } else {
            movement = moveForwards(speedFactor, maxMovement);
        }
        return movement;
    }

    @Override
    public double getEnergyExpenditureFactor() {
        return energyExpenditureFactor;
    }

    @Override
    public void faceTowards(Position.Immutable position2) {
        orientation = position.directionTo(position2);
    }

    @Override
    public Position.Immutable getPosition() {
        return position.toImmutable();
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

    protected void turn(Orientation direction) {
        orientation = orientation.turn(direction);
    }
}
