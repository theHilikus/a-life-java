package com.github.thehilikus.alife.agent.motion;

import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.vision.api.ScanResult;

import javax.validation.constraints.DecimalMax;
import java.util.Map;
import java.util.SortedSet;

/**
 * Motion that moves always in the direction being faced and random turns
 */
public class RandomWalk extends Legs {
    @DecimalMax("1.0")
    private final double turningProbability;


    public RandomWalk(int worldWidth, int worldHeight, int agentId, Position position, Genome genome) {
        super(worldWidth, worldHeight, agentId, position, genome);
        this.turningProbability = genome.getGene(PARAMETER_PREFIX + "turningProbability");
    }

    @Override
    public double move(double speedFactor, SortedSet<ScanResult> scanResults) {
        if (shouldTurn()) {
            turn(Turn.LEFT);
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
