package com.github.thehilikus.alife.agents.animals.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.animals.motions.Legs;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
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
    private final Vision vision;
    private final Legs legs;
    private final double speedFactor;
    private final Agent target;
    private int lastMovement;

    public Hunting(MoodController moodController, Vision vision, Legs legs, Genome genome, Agent target) {
        this.moodController = moodController;
        this.vision = vision;
        this.legs = legs;
        this.speedFactor = genome.getGene(Locomotion.PARAMETER_PREFIX + "huntSpeedFactor");
        this.target = target;
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
    public Mood tick() {
        //scan to see if target is still there
        SortedSet<ScanResult> scanResult = vision.scan(target.getClass()::isInstance);
        Optional<ScanResult> targetOptional = scanResult.stream().filter(scan -> scan.getAgent().getId() == this.target.getId()).findFirst();
        if (targetOptional.isPresent()) {
            ScanResult targetScan = targetOptional.get();
            Orientation targetDirection = legs.getPosition().directionTo(targetScan.getAgent().getPosition());
            int maxMovement = Math.max(Math.abs(targetScan.getXDistance()) - 1, Math.abs(targetScan.getYDistance()) - 1);
            if (maxMovement == 0) {
                return reachedTarget();
            } else {
                lastMovement = legs.move(speedFactor, targetDirection, maxMovement);
            }
        } else {
            LOG.info("Target {} is gone :(", target.getId());
            return moodController.startScouting();
        }

        return this;
    }

    protected Mood reachedTarget() {
        return moodController.startEating((Agent.Eatable) target);
    }

    @Override
    public int getEnergyDelta() {
        return EnergyTracker.ENERGY_DERIVATIVE + (int) Math.round(lastMovement * legs.getEnergyExpenditureFactor());
    }

    @Override
    public String toString() {
        return "Hunting{" +
                "target=" + target.getId() +
                '}';
    }

    @Override
    public Ansi.FColor getTerminalColour() {
        return Ansi.FColor.BLUE;
    }
}
