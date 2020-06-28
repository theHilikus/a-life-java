package com.github.thehilikus.alife.agents.animals.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.controllers.HungerTracker;
import com.github.thehilikus.alife.api.MoodController;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.agents.plants.Plant;
import com.github.thehilikus.alife.api.Locomotion;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.ScanResult;
import com.github.thehilikus.alife.api.Vision;

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
    private final int agentId;
    private int lastMovement;
    private final double speedFactor;


    public Scouting(MoodController moodController, Vision vision, Locomotion locomotion, Genome genome) {
        this.moodController = moodController;
        this.agentId = vision.getAgentId();
        this.vision = vision;
        this.locomotion = locomotion;
        this.speedFactor = genome.getGene(Locomotion.PARAMETER_PREFIX + "scoutSpeedFactor");
    }

    @Override
    public Mood tick() {
        //scout the area
        SortedSet<ScanResult> foundAgents = vision.scan(Plant.class);
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
    public int getHungerDelta() {
        return HungerTracker.HUNGER_DERIVATIVE;
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
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "current", getClass().getSimpleName(),
                PARAMETER_PREFIX + "speedFactor", Double.toString(speedFactor),
                PARAMETER_PREFIX + "lastMovement", Integer.toString(lastMovement)
        );
    }

    @Override
    public String toString() {
        return "Scouting{}";
    }
}
