package com.github.thehilikus.alife.agents.animals.motions;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Position;
import com.github.thehilikus.alife.api.Locomotion;
import com.github.thehilikus.alife.api.Orientation;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Motion that always moves forward until reaching the edge of the world
 */
public class StraightWalk implements Locomotion {
    private static final Logger LOG = LoggerFactory.getLogger(StraightWalk.class.getSimpleName());
    private final int agentId;
    private final int topSpeed;
    private final double energyExpenditureFactor;

    private final Position position;
    private final int worldWidth;
    private final int worldHeight;

    public StraightWalk(int agentId, Position position, Genome genome, World world) {
        this.agentId = agentId;
        this.topSpeed = genome.getGene(Locomotion.PARAMETER_PREFIX + "topSpeed");
        this.energyExpenditureFactor = genome.getGene(Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor");
        this.position = position;
        this.worldWidth = world.getWidth();
        this.worldHeight = world.getHeight();
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
                result = worldWidth - position.getX() - 1;
                break;
        }

        return result;
    }

    @Override
    public double getEnergyExpenditureFactor() {
        return energyExpenditureFactor;
    }

    @Override
    public Position.Immutable getPosition() {
        return new Position.Immutable(position);
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "topSpeed", Integer.toString(topSpeed),
                PARAMETER_PREFIX + "energyExpenditureFactor", Double.toString(energyExpenditureFactor)
        );
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }
}
