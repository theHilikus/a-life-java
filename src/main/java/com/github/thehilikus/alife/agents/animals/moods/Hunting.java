package com.github.thehilikus.alife.agents.animals.moods;

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
    private static final int PRIORITY = 82;
    private static final Logger LOG = LoggerFactory.getLogger(Hunting.class);
    private final MoodController moodController;
    private final Vision vision;
    private final Locomotion locomotion;
    private final double speedFactor;
    private final Agent target;
    private int lastMovement;

    public Hunting(MoodController moodController, Vision vision, Locomotion locomotion, Genome genome, Agent target) {
        this.moodController = moodController;
        this.vision = vision;
        this.locomotion = locomotion;
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
                PARAMETER_PREFIX + "lastMovement", lastMovement,
                PARAMETER_PREFIX + "target", target.getId(),
                PARAMETER_PREFIX + "targetPosition", target.getPosition()
        );
    }

    @Override
    public Mood tick() {
        //scan to see if target is still there
        SortedSet<ScanResult> scanResult = vision.scan(target.getClass()::isInstance);
        Optional<ScanResult> targetOptional = scanResult.stream().filter(scan -> scan.getAgent().getId() == this.target.getId()).findFirst();
        if (targetOptional.isPresent()) {
            ScanResult targetScan = targetOptional.get();
            int maxMovement = locomotion.moveTowardsTarget(speedFactor, (int) Math.sqrt(targetScan.getDistanceSquared()), targetScan.getRelativeDirection());
            if (locomotion.getPosition().isNextTo(targetScan.getAgent().getPosition())) {
                return reachedTarget();
            } else {
                lastMovement = maxMovement;
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
        return EnergyTracker.ENERGY_DERIVATIVE + (int) Math.round(lastMovement * locomotion.getEnergyExpenditureFactor());
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public String toString() {
        return "Hunting{" +
                "target=" + target.getId() +
                '}';
    }
}
