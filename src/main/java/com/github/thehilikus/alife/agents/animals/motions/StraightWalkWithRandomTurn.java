package com.github.thehilikus.alife.agents.animals.motions;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Locomotion;
import com.github.thehilikus.alife.api.Orientation;
import com.github.thehilikus.alife.api.Position;
import com.github.thehilikus.alife.world.RandomProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.DecimalMax;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Motion that moves always in the direction being faced and random turns
 */
public class StraightWalkWithRandomTurn implements Locomotion {
    private static final Logger LOG = LoggerFactory.getLogger(StraightWalkWithRandomTurn.class.getSimpleName());
    private final int agentId;
    private final Legs walker;
    @DecimalMax("1.0")
    private final double turningProbability;
    private Orientation orientation;


    public StraightWalkWithRandomTurn(int agentId, Position position, Genome genome) {
        this.agentId = agentId;
        this.turningProbability = genome.getGene(Locomotion.PARAMETER_PREFIX + "turningProbability");
        this.orientation = Orientation.fromInt(RandomProvider.nextInt(4));
        this.walker = new Legs(agentId, position, genome);
    }

    @Override
    public int move(double speedFactor, int maxMovement) {
        int result = 0;
        if (maxMovement == 0) {
            //we are next to the edge. Turn and move one
            turn();
            result = walker.move(speedFactor, orientation, 1);
        } else if (shouldTurn()) {
            turn();
        } else {
            result = walker.move(speedFactor, orientation, maxMovement);
        }

        return result;
    }

    @Override
    public double getEnergyExpenditureFactor() {
        return walker.getEnergyExpenditureFactor();
    }

    @Override
    public Position.Immutable getPosition() {
        return walker.getPosition();
    }

    private void turn() {
        Orientation direction = Orientation.WEST;
        LOG.info("Turning {}", direction);
        orientation = orientation.turn(direction);
    }

    private boolean shouldTurn() {
        double draw = RandomProvider.nextDouble(1);
        return draw < turningProbability;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(PARAMETER_PREFIX + "orientation", orientation.toString());
        result.put(PARAMETER_PREFIX + "turningProbability", Double.toString(turningProbability));
        result.putAll(walker.getParameters());

        return result;
    }
}
