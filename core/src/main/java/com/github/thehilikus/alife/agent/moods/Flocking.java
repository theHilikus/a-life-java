package com.github.thehilikus.alife.agent.moods;

import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.motion.api.CartesianVector;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.motion.api.PolarVector;
import com.github.thehilikus.alife.agent.vision.api.ScanResult;
import com.github.thehilikus.alife.agent.vision.api.Vision;
import com.github.thehilikus.alife.agent.vitals.AgentModules;
import com.github.thehilikus.alife.agent.vitals.EnergyTracker;

import java.util.Map;
import java.util.SortedSet;

public class Flocking implements Mood {
    private static final int PRIORITY = 10;
    private final int separation;
    private final int alignment;
    private final int cohesion;
    private final Vision vision;
    private final Locomotion locomotion;
    private double lastMovementEnergy;

    public Flocking(AgentModules dependencies) {
        Genome genome = dependencies.getGenome();
        this.vision = dependencies.getVision();
        this.locomotion = dependencies.getLocomotion();
        this.separation = genome.getGene(Locomotion.PARAMETER_PREFIX + "separation");
        this.alignment = genome.getGene(Locomotion.PARAMETER_PREFIX + "alignment");
        this.cohesion = genome.getGene(Locomotion.PARAMETER_PREFIX + "cohesion");
    }

    @Override
    public int getAgentId() {
        return vision.getAgentId();
    }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of(
                PARAMETER_PREFIX + "separation", separation,
                PARAMETER_PREFIX + "alignment", alignment,
                PARAMETER_PREFIX + "cohesion", cohesion
        );
    }

    @Override
    public Mood tick(LivingAgent me) {
        SortedSet<ScanResult> flockScan = vision.scan(other -> other instanceof LivingAgent && ((LivingAgent) other).getMood().getClass().equals(Flocking.class));

        CartesianVector separationCorrection = calculateSeparationCorrection(flockScan);
//        CartesianVector alignmentCorrection = calculateAlignmentCorrection(flockScan);
//        CartesianVector cohesionCorrection = calculateCohesionCorrection(flockScan);

//        CartesianVector totalCorrection = separationCorrection.plus(alignmentCorrection.plus(cohesionCorrection));
        CartesianVector totalCorrection = separationCorrection;
        Position.Immutable desiredPosition = new Position(locomotion.getPosition().getX() + totalCorrection.getX(), locomotion.getPosition().getY() + totalCorrection.getY()).toImmutable();
        lastMovementEnergy = locomotion.moveTowardsTarget(1, desiredPosition);

        return this;
    }

    private CartesianVector calculateSeparationCorrection(SortedSet<ScanResult> flockScan) {
        CartesianVector result = new CartesianVector(0, 0);
        CartesianVector me = new CartesianVector(locomotion.getPosition().getX(), locomotion.getPosition().getY());
        for (var boidScan : flockScan) {
            if (boidScan.getDistanceSquared() < separation * separation) {
                CartesianVector boidVector = new CartesianVector(boidScan.getAgent().getPosition().getX(), boidScan.getAgent().getPosition().getY());
                CartesianVector separation = me.subtract(boidVector);
                CartesianVector normalizedSeparation = separation.normalize();
                CartesianVector weightedSeparation = normalizedSeparation.divide(Math.sqrt(boidScan.getDistanceSquared()));
                result = result.plus(weightedSeparation);
            }
        }
        if (flockScan.size() > 1) {
            result = result.divide(flockScan.size());
        }

        if (result.getMagnitude() > 0) {
            result = result.normalize();
        }

        return result;
    }

    private CartesianVector calculateAlignmentCorrection(SortedSet<ScanResult> flockScan) {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    private CartesianVector calculateCohesionCorrection(SortedSet<ScanResult> flockScan) {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public int getEnergyDelta() {
        return EnergyTracker.ENERGY_DERIVATIVE + (int) Math.round(lastMovementEnergy);
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public String toString() {
        return "Flocking{" +
                "separation=" + separation +
                ", alignment=" + alignment +
                ", cohesion=" + cohesion +
                '}';
    }
}
