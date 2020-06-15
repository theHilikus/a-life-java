package com.github.thehilikus.alife.agents.animals.motions;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Coordinates;
import com.github.thehilikus.alife.api.Locomotion;
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
public class StraightWalkWithRandomTurn implements Locomotion {
    private static final Logger LOG = LoggerFactory.getLogger(StraightWalkWithRandomTurn.class.getSimpleName());
    private final int agentId;
    private final StraightWalk walker;
    private final int turningProbability;
    private Orientation orientation;


    public StraightWalkWithRandomTurn(int agentId, Genome genome, World world) {
        this.agentId = agentId;
        this.turningProbability = genome.getGene(Locomotion.PARAMETER_PREFIX + "turningProbability");
        this.orientation = Orientation.fromInt(RandomSource.nextInt(4));
        this.walker = new StraightWalk(agentId, genome, world);
    }

    @Override
    public int move(double speedFactor, Orientation direction) {
        int result = 0;
        if (shouldTurn()) {
            turn();
        } else {
            result = walker.move(speedFactor, orientation);
        }

        return result;
    }

    @Override
    public double getEnergyExpenditureFactor() {
        return walker.getEnergyExpenditureFactor();
    }

    @Override
    public Coordinates.Immutable getPosition() {
        return walker.getPosition();
    }

    private void turn() {
        Orientation direction = Orientation.WEST;
        LOG.info("Turning {}", direction);
        orientation = orientation.turn(direction);
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
        Coordinates.Immutable position = getPosition();
        return position.getX() == 0 && orientation == Orientation.WEST
                || position.getX() == walker.getWoldWidth() - 1 && orientation == Orientation.EAST
                || position.getY() == 0 && orientation == Orientation.NORTH
                || position.getY() == walker.getWorldHeight() - 1 && orientation == Orientation.SOUTH;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> result = new LinkedHashMap<>(walker.getParameters());
        result.put(PARAMETER_PREFIX + "orientation", orientation.toString());
        result.put(PARAMETER_PREFIX + "turningProbability", Integer.toString(turningProbability));

        return result;
    }
}
