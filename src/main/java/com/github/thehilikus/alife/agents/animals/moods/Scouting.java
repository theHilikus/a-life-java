package com.github.thehilikus.alife.agents.animals.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.genetics.Genome;
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
    private final Vision vision;
    private final Locomotion locomotion;
    private final MoodController moodController;
    private int lastMovement;
    private final double speedFactor;


    public Scouting(MoodController moodController, Vision vision, Locomotion locomotion, Genome genome) {
        this.moodController = moodController;
        this.vision = vision;
        this.locomotion = locomotion;
        this.speedFactor = genome.getGene(Locomotion.PARAMETER_PREFIX + "scoutSpeedFactor");
    }

    @Override
    public Mood tick() {
        //scout the area
        SortedSet<ScanResult> foundAgents = vision.scan(agent -> agent instanceof Plant || agent instanceof Edge);
        if (!foundAgents.isEmpty()) {
            Optional<Plant> plantOptional = foundAgents.stream().map(ScanResult::getAgent).filter(Plant.class::isInstance).map(Plant.class::cast).findFirst();
            if (plantOptional.isPresent()) {
                return moodController.startHunting(plantOptional.get());
            } else {
                //only found edges
                ScanResult closestEdgeScan = foundAgents.first();
                int maxMovement = Math.max(Math.abs(closestEdgeScan.getXDistance()) - 1, Math.abs(closestEdgeScan.getYDistance()) - 1);
                lastMovement = locomotion.move(speedFactor, maxMovement);
            }
        } else {
            lastMovement = locomotion.move(speedFactor);
        }

        return this;
    }

    @Override
    public int getEnergyDelta() {
        return EnergyTracker.ENERGY_DERIVATIVE + (int) Math.round(lastMovement * locomotion.getEnergyExpenditureFactor());
    }

    @Override
    public Ansi.FColor getTerminalColour() {
        return Ansi.FColor.CYAN;
    }

    @Override
    public int getAgentId() {
        return vision.getAgentId();
    }

    @Override
    public Map<String, Object> getParameters() {
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
