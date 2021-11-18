package com.github.thehilikus.alife.agent.motion;

import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.vision.api.ScanResult;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.api.RandomProvider;
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
        this.turningProbability = genome.getGene(PARAMETER_PREFIX + "turningProbability");
    }

    @Override
    public int move(double speedFactor, SortedSet<ScanResult> scanResults) {
        if (shouldTurn()) {
            int originalOrientation = getOrientation();
            turn(LEFT_TURN);
            LOG.info("Turned from {}° to {}°", originalOrientation, getOrientation());
        } else {
            return super.move(speedFactor, scanResults);
        }
        return 0;
    }

    private boolean shouldTurn() {
        double draw = RandomProvider.nextDouble(1);
        return draw < turningProbability;
    }

    @Override
    public Map<String, Object> getDetails() {
        Map<String, Object> result = super.getDetails();
        result.put(PARAMETER_PREFIX + "turningProbability", turningProbability);

        return result;
    }
}
