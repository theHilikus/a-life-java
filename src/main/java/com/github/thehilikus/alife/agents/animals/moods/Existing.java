package com.github.thehilikus.alife.agents.animals.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.Edge;

import java.util.Map;
import java.util.SortedSet;

/**
 * The basic behaviour of an agent, regardless of mood
 */
public class Existing implements Mood {
    /**
     * The number of hunger points to lose per round
     */
    public static final int HUNGER_DERIVATIVE = -2;
    /**
     * The number of energy points to lose per round
     */
    public static final int ENERGY_DERIVATIVE = -2;
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
        return null;
    }

    @Override
    public int getHungerDelta() {
        return HUNGER_DERIVATIVE;
    }

    @Override
    public int getEnergyDelta() {
        return ENERGY_DERIVATIVE + (int) Math.round(lastMovement * locomotion.getEnergyExpenditureFactor());
    }

    @Override
    public Ansi.FColor getTerminalColour() {
        return Ansi.FColor.NONE;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
                VitalSign.PARAMETER_PREFIX + "energyRateOfChange", Integer.toString(ENERGY_DERIVATIVE),
                VitalSign.PARAMETER_PREFIX + "hungerRateOfChange", Integer.toString(HUNGER_DERIVATIVE)
        );
    }
}
