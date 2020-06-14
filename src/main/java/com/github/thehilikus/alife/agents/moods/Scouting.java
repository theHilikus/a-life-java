package com.github.thehilikus.alife.agents.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.FoodAgent;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.Motion;
import com.github.thehilikus.alife.api.ScanResult;
import com.github.thehilikus.alife.api.Vision;

import java.util.Map;
import java.util.SortedSet;

/**
 * When looking for food but it has not been found
 */
public class Scouting implements Mood {
    private final Vision vision;
    private final Motion motion;
    private final Existing existing;
    private final int agentId;
    private int lastMovement;


    public Scouting(Vision vision, Motion motion) {
        this.agentId = vision.getAgentId();
        this.vision = vision;
        this.motion = motion;
        existing = new Existing(agentId);
    }

    @Override
    public Mood tick() {
        //scout the area
        SortedSet<ScanResult> foundAgents = vision.scan(FoodAgent.class);
        if (!foundAgents.isEmpty()) {
            return new Hunting(foundAgents.first().getAgent());
        } else {
            lastMovement = motion.move(Motion.SpeedType.SCOUT);
        }

        return this;
    }

    @Override
    public int getHungerDelta() {
        return existing.getHungerDelta();
    }

    @Override
    public int getEnergyDelta() {
        return existing.getEnergyDelta() + (int) Math.round(lastMovement * motion.getEnergyExpenditureFactor());
    }

    @Override
    public Ansi.FColor getTerminalColour() {
        return Ansi.FColor.BLUE;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "current", getClass().getSimpleName(),
                PARAMETER_PREFIX + "lastMovement", Integer.toString(lastMovement)
        );
    }
}
