package com.github.thehilikus.alife.agents.animals.moods;

import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.Edge;

import java.util.Map;
import java.util.SortedSet;

/**
 * The basic behaviour of an agent, regardless of mood
 */
public class Existing implements Mood {
    private static final int PRIORITY = 10;

    private final Vision vision;
    private final Locomotion locomotion;
    private final double speedFactor;
    private int lastMovement;

    public Existing(Vision vision, Genome genome, Locomotion locomotion) {
        this.vision = vision;
        this.locomotion = locomotion;
        this.speedFactor = genome.getGene(Locomotion.PARAMETER_PREFIX + "idleSpeedFactor");
    }

    @Override
    public Mood tick() {
        SortedSet<ScanResult> foundAgents = vision.scan(Edge.class::isInstance);
        lastMovement = locomotion.move(speedFactor, foundAgents);

        return this;
    }

    @Override
    public int getEnergyDelta() {
        return EnergyTracker.ENERGY_DERIVATIVE + (int) Math.round(lastMovement * locomotion.getEnergyExpenditureFactor());
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public int getAgentId() {
        return vision.getAgentId();
    }

    @Override
    public Map<String, Object> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "speedFactor", speedFactor
        );
    }

    @Override
    public String toString() {
        return "Existing{}";
    }
}
