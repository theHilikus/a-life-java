package com.github.thehilikus.alife.agents.animals.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Locomotion;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.ScanResult;
import com.github.thehilikus.alife.api.Vision;
import com.github.thehilikus.alife.world.Edge;

import java.util.Map;
import java.util.SortedSet;

/**
 * The basic behaviour of an agent, regardless of mood
 */
public class Existing implements Mood {
    private final Vision vision;
    private final int agentId;
    private final Locomotion locomotion;
    private final double speedFactor;
    private int lastMovement;

    public Existing(Vision vision, Genome genome, Locomotion locomotion) {
        this.vision = vision;
        this.agentId = locomotion.getAgentId();
        this.locomotion = locomotion;
        this.speedFactor = genome.getGene(Locomotion.PARAMETER_PREFIX + "idleSpeedFactor");
    }

    @Override
    public Mood tick() {
        SortedSet<ScanResult> foundAgents = vision.scan(Edge.class);
        if (!foundAgents.isEmpty()) {
            ScanResult closestEdgeScan = foundAgents.first();
            int maxMovement = Math.max(Math.abs(closestEdgeScan.getXDistance()) - 1, Math.abs(closestEdgeScan.getYDistance()) - 1);
            lastMovement = locomotion.move(speedFactor, maxMovement);
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
        return Ansi.FColor.WHITE;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "current", getClass().getSimpleName(),
                PARAMETER_PREFIX + "speedFactor", Double.toString(speedFactor)
        );
    }

    @Override
    public String toString() {
        return "Existing{}";
    }
}
