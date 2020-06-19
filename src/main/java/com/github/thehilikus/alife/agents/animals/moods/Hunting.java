package com.github.thehilikus.alife.agents.animals.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.animals.motions.Legs;
import com.github.thehilikus.alife.agents.controllers.MoodController;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

/**
 * When food has been located
 */
public class Hunting implements Mood {
    private static final Logger LOG = LoggerFactory.getLogger(Hunting.class);
    private final MoodController moodController;
    private final int agentId;
    private final Vision vision;
    private final Legs legs;
    private final double speedFactor;
    private final Existing existing;
    private final Agent target;
    private int lastMovement;

    public Hunting(MoodController moodController, Vision vision, Legs legs, Genome genome, Agent foodAgent) {
        this.moodController = moodController;
        this.agentId = vision.getAgentId();
        this.vision = vision;
        this.legs = legs;
        this.speedFactor = genome.getGene(Locomotion.PARAMETER_PREFIX + "huntSpeedFactor");
        this.existing = new Existing(agentId);
        this.target = foodAgent;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "current", getClass().getSimpleName(),
                PARAMETER_PREFIX + "huntSpeedFactor", Double.toString(speedFactor),
                PARAMETER_PREFIX + "lastMovement", Integer.toString(lastMovement)
        );
    }

    @Override
    public Mood tick() {
        //scan to see if target is still there
        SortedSet<ScanResult> scanResult = vision.scan(target.getClass());
        Optional<ScanResult> targetOptional = scanResult.stream().filter(scan -> scan.getAgent().getId() == this.target.getId()).findFirst();
        if (targetOptional.isPresent()) {
            ScanResult targetScan = targetOptional.get();
            Orientation targetDirection = findTargetDirection(targetScan);
            int maxMovement = Math.max(Math.abs(targetScan.getXDistance()) - 1, Math.abs(targetScan.getYDistance()) - 1);
            if (maxMovement == 0) {
                throw new UnsupportedOperationException("EATING");
            } else {
                lastMovement = legs.move(speedFactor, targetDirection, maxMovement);
            }
        } else {
            LOG.info("Target {} is gone :(", target.getId());
            return moodController.startScouting();
        }

        return this;
    }

    private Orientation findTargetDirection(ScanResult targetScan) {
        int deltaX = targetScan.getXDistance();
        int deltaY = targetScan.getYDistance();
        Orientation result;
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            //move in X
            if (deltaX < 0) {
                result = Orientation.WEST;
            } else {
                result = Orientation.EAST;
            }
        } else {
            //move in Y
            if (deltaY < 0) {
                result = Orientation.NORTH;
            } else {
                result = Orientation.SOUTH;
            }
        }

        return result;
    }

    @Override
    public int getHungerDelta() {
        return existing.getHungerDelta();
    }

    @Override
    public int getEnergyDelta() {
        return existing.getEnergyDelta() + (int) Math.round(lastMovement * legs.getEnergyExpenditureFactor());
    }

    @Override
    public String toString() {
        return "Hunting{" +
                "target=" + target +
                '}';
    }

    @Override
    public Ansi.FColor getTerminalColour() {
        return Ansi.FColor.YELLOW;
    }
}
