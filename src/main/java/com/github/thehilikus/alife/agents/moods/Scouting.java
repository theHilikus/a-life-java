package com.github.thehilikus.alife.agents.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.FoodAgent;
import com.github.thehilikus.alife.agents.controllers.MoodController;
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
    private final MoodController moodController;
    private final int agentId;
    private int lastMovement;
    private final double speedFactor;


    public Scouting(MoodController moodController, Vision vision, Motion motion, double speedFactor) {
        this.moodController = moodController;
        this.agentId = vision.getAgentId();
        this.vision = vision;
        this.motion = motion;
        this.speedFactor = speedFactor;
        existing = new Existing(agentId);
    }

    @Override
    public Mood tick() {
        //scout the area
        SortedSet<ScanResult> foundAgents = vision.scan(FoodAgent.class);
        if (!foundAgents.isEmpty()) {
            return moodController.startHunting(agentId, foundAgents.first().getAgent());
        } else {
            lastMovement = motion.move(speedFactor, null);
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
                PARAMETER_PREFIX + "scoutSpeedFactor", Double.toString(speedFactor),
                PARAMETER_PREFIX + "lastMovement", Integer.toString(lastMovement)
        );
    }
}
