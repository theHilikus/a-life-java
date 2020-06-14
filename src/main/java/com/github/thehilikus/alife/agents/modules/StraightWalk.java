package com.github.thehilikus.alife.agents.modules;

import com.github.thehilikus.alife.api.Coordinates;
import com.github.thehilikus.alife.api.Motion;
import com.github.thehilikus.alife.api.Orientation;
import com.github.thehilikus.alife.world.RandomSource;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StraightWalk implements Motion {
    private static final Logger LOG = LoggerFactory.getLogger(StraightWalk.class.getSimpleName());
    private final int agentId;
    private final int topSpeed;
    private final double energyExpenditureFactor;

    private final Coordinates position;
    private final int woldWidth;
    private final int worldHeight;

    private StraightWalk(int agentId, int topSpeed, double energyExpenditureFactor, Coordinates position, int woldWidth, int worldHeight) {
        this.agentId = agentId;
        this.topSpeed = topSpeed;
        this.energyExpenditureFactor = energyExpenditureFactor;
        this.position = position;
        this.woldWidth = woldWidth;
        this.worldHeight = worldHeight;
    }

    public static StraightWalk create(int id, World world) {
        Coordinates startingPosition = world.getEmptyPosition();
        int topSpeed = RandomSource.nextInt(20);
        double energyExpenditureFactor = RandomSource.nextDouble(1);

        return new StraightWalk(id, topSpeed, energyExpenditureFactor, startingPosition, world.getWidth(), world.getHeight());
    }

    @Override
    public int move(double speedFactor, Orientation direction) {
        int maxDistance = getDistanceToEdge(direction);
        int movementDelta = Math.min(maxDistance, (int) Math.round(topSpeed * speedFactor));
        LOG.info("Walking {} {} spaces", direction, movementDelta);
        position.move(direction, movementDelta);

        return movementDelta;
    }

    private int getDistanceToEdge(Orientation orientation) {
        int result = Integer.MAX_VALUE;
        switch (orientation) {
            case NORTH:
                result = position.getY();
                break;
            case WEST:
                result = position.getX();
                break;
            case SOUTH:
                result = worldHeight - position.getY() - 1;
                break;
            case EAST:
                result = woldWidth - position.getX() - 1;
                break;
        }

        return result;
    }

    @Override
    public double getEnergyExpenditureFactor() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public Coordinates.Immutable getPosition() {
        return new Coordinates.Immutable(position);
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "position", position.getX() + ", " + position.getY(),
                PARAMETER_PREFIX + "topSpeed", Integer.toString(topSpeed),
                PARAMETER_PREFIX + "energyExpenditureFactor", Double.toString(energyExpenditureFactor)
        );
    }

    public int getWoldWidth() {
        return woldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }
}
