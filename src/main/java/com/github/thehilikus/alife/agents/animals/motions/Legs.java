package com.github.thehilikus.alife.agents.animals.motions;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Locomotion;
import com.github.thehilikus.alife.api.Orientation;
import com.github.thehilikus.alife.api.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Motion that always moves forward until reaching the edge of the world
 */
public class Legs {
    private static final Logger LOG = LoggerFactory.getLogger(Legs.class.getSimpleName());
    private final int agentId;
    private final int topSpeed;

    @DecimalMin("-1.0")
    @DecimalMax("0.0")
    private final double energyExpenditureFactor;

    private Position position;
    private Orientation lastMoveDirection;

    public Legs(int agentId, Position position, Genome genome) {
        this.agentId = agentId;
        this.topSpeed = genome.getGene(Locomotion.PARAMETER_PREFIX + "topSpeed");
        this.energyExpenditureFactor = genome.getGene(Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor");
        this.position = position;
    }

    public int move(double speedFactor, Orientation direction, int maxMovement) {
        int movementDelta = (int) Math.ceil(topSpeed * speedFactor);
        movementDelta = Math.min(movementDelta, maxMovement);
        if (movementDelta != maxMovement) {
            LOG.info("Walking {} {} spaces", direction, movementDelta);
        } else {
            LOG.info("Walking {} only {} spaces because it is close to the target", direction, movementDelta);
        }
        lastMoveDirection = direction;
        position.move(direction, movementDelta);

        return movementDelta;
    }

    public double getEnergyExpenditureFactor() {
        return energyExpenditureFactor;
    }

    public Position.Immutable getPosition() {
        return position.toImmutable();
    }

    public void setPosition(Position newPosition) {
        position = newPosition;
    }

    public int getAgentId() {
        return agentId;
    }

    public Map<String, Object> getParameters() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(Locomotion.PARAMETER_PREFIX + "topSpeed", topSpeed);
        result.put(Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor", energyExpenditureFactor);
        if (lastMoveDirection != null) {
            result.put(Locomotion.PARAMETER_PREFIX + "orientation", lastMoveDirection);
        }

        return result;
    }
}
