package com.github.thehilikus.alife.agents.animals.moods;

import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.plants.Plant;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.Edge;

import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

/**
 * When looking for food but it has not been found
 */
public class Scouting implements Mood {
    /**
     * The priority of the mood from 1-100
     */
    private static final int PRIORITY = 80;
    private final Vision vision;
    private final Locomotion locomotion;
    private final AgentModules dependencies;
    private int lastMovement;
    private final double speedFactor;


    public Scouting(AgentModules dependencies) {
        this.dependencies = dependencies;
        this.vision = dependencies.getVision();
        this.locomotion = dependencies.getLocomotion();
        this.speedFactor = dependencies.getGenome().getGene(Locomotion.PARAMETER_PREFIX + "scoutSpeedFactor");
    }

    @Override
    public Mood tick(Agent.Living me) {
        //scout the area
        SortedSet<ScanResult> foundAgents = vision.scan(agent -> agent instanceof Plant || agent instanceof Edge);
        if (!foundAgents.isEmpty()) {
            Optional<ScanResult> plantScanOptional = foundAgents.stream().filter(scan -> scan.getAgent() instanceof Plant).findFirst();
            if (plantScanOptional.isPresent()) {
                ScanResult plantScan = plantScanOptional.get();
                locomotion.turn(plantScan.getRelativeDirection());
                return new Hunting(dependencies, plantScan.getAgent());
            } else {
                //only found edges
                lastMovement = locomotion.move(speedFactor, foundAgents);
            }
        } else {
            lastMovement = locomotion.move(speedFactor, foundAgents);
        }

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
                PARAMETER_PREFIX + "speedFactor", speedFactor,
                PARAMETER_PREFIX + "lastMovement", lastMovement
        );
    }

    @Override
    public String toString() {
        return "Scouting{}";
    }
}
