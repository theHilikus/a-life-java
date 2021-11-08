package com.github.thehilikus.alife.agents.animals.motions;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.RandomProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.DecimalMax;
import java.util.Map;
import java.util.SortedSet;

/**
 * Motion that moves always in the direction being faced and random turns
 */
public class StraightWalkWithRandomTurn extends Legs {
    private static final Logger LOG = LoggerFactory.getLogger(StraightWalkWithRandomTurn.class.getSimpleName());
    @DecimalMax("1.0")
    private final double turningProbability;


    public StraightWalkWithRandomTurn(int agentId, Position position, Genome genome) {
        super(agentId, position, genome);
        this.turningProbability = genome.getGene(Locomotion.PARAMETER_PREFIX + "turningProbability");
    }

    @Override
    public int move(double speedFactor, SortedSet<ScanResult> scanResults) {
        if (shouldTurn()) {
            int originalOrientation = getOrientation();
            turn(Orientation.LEFT_TURN);
            LOG.info("Turned from {}° to {}°", originalOrientation, getOrientation());
        } else {
            super.move(speedFactor, scanResults);
        }
        return 0;
    }

    private boolean shouldTurn() {
        double draw = RandomProvider.nextDouble(1);
        return draw < turningProbability;
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> result = super.getParameters();
        result.put(PARAMETER_PREFIX + "turningProbability", turningProbability);

        return result;
    }
}
