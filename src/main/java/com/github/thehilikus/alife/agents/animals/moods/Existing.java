package com.github.thehilikus.alife.agents.animals.moods;

import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
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


    public Existing(AgentModules dependencies) {
        this.vision = dependencies.getVision();
        this.locomotion = dependencies.getLocomotion();
        this.speedFactor = dependencies.getGenome().getGene(Locomotion.PARAMETER_PREFIX + "idleSpeedFactor");
    }

    @Override
    public Mood tick(Agent.Living me) {
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
    public Map<String, Object> getDetails() {
        return Map.of(
                PARAMETER_PREFIX + "speedFactor", speedFactor
        );
    }

    @Override
    public String toString() {
        return "Existing{}";
    }
}
