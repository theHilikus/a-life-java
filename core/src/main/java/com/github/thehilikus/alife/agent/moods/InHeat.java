package com.github.thehilikus.alife.agent.moods;

import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.api.internal.SocialAgent;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.social.Herbivore;
import com.github.thehilikus.alife.agent.vision.api.ScanResult;
import com.github.thehilikus.alife.agent.vision.api.Vision;
import com.github.thehilikus.alife.agent.vitals.AgentModules;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;

/**
 * When an agent is looking for a suitable mate to reproduce
 */
public class InHeat implements Mood {
    private static final int PRIORITY = 60;
    private final Existing existing;
    private final Vision vision;
    private final Locomotion locomotion;
    private final AgentModules dependencies;

    public InHeat(AgentModules dependencies) {
        this.dependencies = dependencies;
        this.existing = new Existing(dependencies);
        this.vision = dependencies.getVision();
        this.locomotion = dependencies.getLocomotion();
    }

    @Override
    public @NotNull Mood tick(LivingAgent me) {
        SortedSet<ScanResult> potentialMates = vision.scan(Herbivore.class::isInstance);
        if (!potentialMates.isEmpty()) {
            Optional<ScanResult> closestGood = getClosestValidMate(potentialMates);
            if (closestGood.isPresent()) {
                locomotion.turn(closestGood.get().getRelativeDirection());
                return new InHeatChasing(dependencies, (SocialAgent) closestGood.get().getAgent());
            }
        }

        // nothing found
        existing.tick(me);
        return this;
    }

    private Optional<ScanResult> getClosestValidMate(Iterable<ScanResult> potentialMates) {
        for (ScanResult potentialMateScan : potentialMates) {
            LivingAgent potentialMate = (LivingAgent) potentialMateScan.getAgent();
            if (potentialMate.getMood() instanceof InHeat || potentialMate.getMood() instanceof InHeatChasing) {
                return Optional.of(potentialMateScan);
            }
        }

        return Optional.empty();
    }

    @Override
    public int getEnergyDelta() {
        return existing.getEnergyDelta();
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public int getAgentId() {
        return existing.getAgentId();
    }

    @Override
    public Map<String, Object> getDetails() {
        return Map.of();
    }

    @Override
    public String toString() {
        return "InHeat{}";
    }
}
