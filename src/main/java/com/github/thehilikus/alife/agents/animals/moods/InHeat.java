package com.github.thehilikus.alife.agents.animals.moods;

import com.github.thehilikus.alife.agents.animals.Herbivore;
import com.github.thehilikus.alife.api.*;

import javax.validation.constraints.NotNull;
import java.util.Map;
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
    public @NotNull Mood tick(Agent.Living me) {
        SortedSet<ScanResult> potentialMates = vision.scan(Herbivore.class::isInstance);
        if (!potentialMates.isEmpty()) {
            ScanResult closest = potentialMates.first();
            locomotion.turn(closest.getRelativeDirection());
            return moodController.startFollowing((Agent.Evolvable) closest.getAgent());
        } else {
            existing.tick(me);
            return this;
        }
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
    public Map<String, Object> getParameters() {
        return Map.of();
    }

    @Override
    public String toString() {
        return "InHeat{}";
    }
}
