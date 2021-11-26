package com.github.thehilikus.alife.agent.motion;

import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.motion.api.PolarVector;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

public class Wings extends Legs {
//    private final int agentId;
//    private final Position position;
//    private int orientation;
//    private double velocityX;
//    private double velocityY;

    @DecimalMin("-1.0")
    @DecimalMax("0.0")
    private final double energyExpenditureFactor = 0.05;


    public Wings(int worldWidth, int worldHeight, int agentId, Position position, Genome genome) {
        super(worldWidth, worldHeight, agentId, position, genome);
    }

    @Override
    public double moveTowardsTarget(double speedFactor, Position.Immutable target) {
        if (target.getX() < 1 || target.getX() > getWorldWidth() - 2 || target.getY() < 1 || target.getY() > getWorldHeight() - 2) {
            int newX = Math.min(Math.max(target.getX(), 1),  getWorldWidth() - 2);
            int newY = Math.min(Math.max(target.getY(), 1),  getWorldHeight() - 2);
            double result = moveTowardsTarget(speedFactor, new Position(newX, newY).toImmutable());
            if (result != 0) {
                turnAfterEdgeCollision();
            }

            return result;
        } else {
            PolarVector vectorToTarget = new PolarVector(getPosition(), getOrientation(), target);
            return moveTo(speedFactor, vectorToTarget); //TODO: decide if we want turn-only behaviour
        }

    }
}
