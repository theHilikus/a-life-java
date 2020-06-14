package com.github.thehilikus.alife.agents.modules;

import com.github.thehilikus.alife.api.Coordinates;
import com.github.thehilikus.alife.api.Motion;
import com.github.thehilikus.alife.api.Orientation;
import com.github.thehilikus.alife.world.RandomSource;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Motion that moves always in the direction being faced and random turns
 */
public class FrontWalkerWithRandomTurn implements Motion {
    private static final Logger LOG = LoggerFactory.getLogger(FrontWalkerWithRandomTurn.class.getSimpleName());
    private final int agentId;
    private Orientation currentOrientation;
    private final Coordinates currentPosition;
    private final int turningProbability;
    private final int woldWidth;
    private final int worldHeight;
    private final Speeds speeds;
    private final double energyExpenditureFactor;

    private FrontWalkerWithRandomTurn(int agentId, int woldWidth, int worldHeight, Coordinates startingPosition, Speeds speeds) {
        this(agentId, Orientation.fromInt(RandomSource.nextInt(4)), startingPosition, RandomSource.nextInt(100), woldWidth, worldHeight, speeds, RandomSource.nextDouble(1));
    }

    private FrontWalkerWithRandomTurn(int agentId, Orientation currentOrientation, Coordinates currentPosition, int turningProbability, int woldWidth, int worldHeight, Speeds speeds, double energyExpenditureFactor) {
        this.agentId = agentId;
        this.currentOrientation = currentOrientation;
        this.currentPosition = currentPosition;
        this.turningProbability = turningProbability;
        this.woldWidth = woldWidth;
        this.worldHeight = worldHeight;
        this.speeds = speeds;
        this.energyExpenditureFactor = energyExpenditureFactor;
    }

    public static Motion create(int id, World world) {
        return new FrontWalkerWithRandomTurn(id, world.getWidth(), world.getHeight(), world.getEmptyPosition(), Speeds.create());
    }

    @Override
    public int move(SpeedType speedType) {
        int result = 0;
        if (shouldTurn()) {
            turn();
        } else {
            result = walk(speedType);
        }

        return result;
    }

    @Override
    public int moveTo(Coordinates destination, SpeedType speedType) {
        int deltaX = destination.getX() - currentPosition.getX();
        int deltaY = destination.getY() - currentPosition.getY();
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            //move in X
            if (deltaX < 0) {
                currentOrientation = Orientation.WEST;
            } else {
                currentOrientation = Orientation.EAST;
            }
        } else {
            //move in Y
            if (deltaY < 0) {
                currentOrientation = Orientation.NORTH;
            } else {
                currentOrientation = Orientation.SOUTH;
            }
        }

        return walk(speedType);
    }

    @Override
    public double getEnergyExpenditureFactor() {
        return energyExpenditureFactor;
    }

    @Override
    public Coordinates.Immutable getPosition() {
        return new Coordinates.Immutable(currentPosition);
    }

    private void turn() {
        Orientation direction = Orientation.WEST;
        LOG.info("Turning {}", direction);
        currentOrientation = currentOrientation.turn(direction);
    }

    private int walk(SpeedType speedType) {
        int maxDistance = getDistanceToEdge();
        int movementDelta = Math.min(maxDistance, getMovementDelta(speedType));
        LOG.info("Walking {} {} spaces", currentOrientation, movementDelta);
        currentPosition.move(currentOrientation, movementDelta);

        return movementDelta;
    }

    private int getDistanceToEdge() {
        int result = Integer.MAX_VALUE;
        switch (currentOrientation) {
            case NORTH:
                result = currentPosition.getY();
                break;
            case WEST:
                result = currentPosition.getX();
                break;
            case SOUTH:
                result = worldHeight - currentPosition.getY() - 1;
                break;
            case EAST:
                result = woldWidth - currentPosition.getX() - 1;
                break;
        }

        return result;
    }

    private int getMovementDelta(SpeedType speedType) {
        int movementDelta = 0;
        switch (speedType) {
            case IDLE:
                movementDelta = (int) Math.round(speeds.topSpeed * speeds.idleSpeedFactor);
                break;
            case SCOUT:
                movementDelta = (int) Math.round(speeds.topSpeed * speeds.scoutSpeedFactor);
                break;
            case ESCAPE:
                movementDelta = (int) Math.round(speeds.topSpeed * speeds.escapeSpeedFactor);
                break;
            case HUNT:
                movementDelta = (int) Math.round(speeds.topSpeed * speeds.huntSpeedFactor);
        }
        return movementDelta;
    }

    private boolean shouldTurn() {
        if (isInEdge()) {
            LOG.debug("Reached edge of the world");
            return true;
        } else {
            int draw = RandomSource.nextInt(100);
            return draw < turningProbability;
        }
    }

    private boolean isInEdge() {
        return currentPosition.getX() == 0 && currentOrientation == Orientation.WEST
                || currentPosition.getX() == woldWidth - 1 && currentOrientation == Orientation.EAST
                || currentPosition.getY() == 0 && currentOrientation == Orientation.NORTH
                || currentPosition.getY() == worldHeight - 1 && currentOrientation == Orientation.SOUTH;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(PARAMETER_PREFIX + "orientation", currentOrientation.toString());
        result.put(PARAMETER_PREFIX + "position", currentPosition.getX() + ", " + currentPosition.getY());
        result.put(PARAMETER_PREFIX + "turningProbability", Integer.toString(turningProbability));
        result.put(PARAMETER_PREFIX + "energyExpenditureFactor", Double.toString(energyExpenditureFactor));
        result.putAll(speeds.getParameters());

        return result;
    }

    public static class Speeds {
        private final int topSpeed;
        private final double huntSpeedFactor;
        private final double idleSpeedFactor;
        private final double scoutSpeedFactor;
        private final double escapeSpeedFactor;

        public Speeds(int topSpeed, double huntSpeedFactor, double idleSpeedFactor, double scoutSpeedFactor, double escapeSpeedFactor) {
            this.topSpeed = topSpeed;
            this.huntSpeedFactor = huntSpeedFactor;
            this.idleSpeedFactor = idleSpeedFactor;
            this.scoutSpeedFactor = scoutSpeedFactor;
            this.escapeSpeedFactor = escapeSpeedFactor;
        }

        public static Speeds create() {
            int topSpeed = RandomSource.nextInt(100);
            double huntSpeedFactor = RandomSource.nextDouble(1.0);
            double idleSpeedFactor = RandomSource.nextDouble(1.0);
            double scoutSpeedFactor = RandomSource.nextDouble(1.0);
            double escapeSpeedFactor = RandomSource.nextDouble(1.0);
            return new Speeds(topSpeed, huntSpeedFactor, idleSpeedFactor, scoutSpeedFactor, escapeSpeedFactor);
        }

        public Map<String, String> getParameters() {
            String speedsPrefix = PARAMETER_PREFIX + "speeds.";
            return Map.of(
                    speedsPrefix + "topSpeed", Integer.toString(topSpeed),
                    speedsPrefix + "idleSpeedFactor", Double.toString(idleSpeedFactor),
                    speedsPrefix + "scoutSpeedFactor", Double.toString(scoutSpeedFactor),
                    speedsPrefix + "huntSpeedFactor", Double.toString(huntSpeedFactor),
                    speedsPrefix + "escapeSpeedFactor", Double.toString(escapeSpeedFactor)
            );
        }
    }
}
