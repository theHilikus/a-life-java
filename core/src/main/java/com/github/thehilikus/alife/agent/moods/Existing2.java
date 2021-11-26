package com.github.thehilikus.alife.agent.moods;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.vision.api.ScanResult;
import com.github.thehilikus.alife.agent.vision.api.Vision;
import com.github.thehilikus.alife.agent.vitals.AgentModules;
import com.github.thehilikus.alife.agent.vitals.EnergyTracker;
import com.github.thehilikus.alife.world.Edge;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

public class Existing2 implements Mood {
    private static final int PRIORITY = 10;
    private static final double LAND_SPEED_FACTOR = 0.05;
    private static final int BASE_FLOCKING_URGE = 10;
    private final Vision vision;
    private final Locomotion locomotion;
    private final AgentModules dependencies;
    private double lastMovementEnergy;

    public Existing2(AgentModules dependencies) {
        this.dependencies = dependencies;
        this.vision = dependencies.getVision();
        this.locomotion = dependencies.getLocomotion();
    }

    @Override
    public int getAgentId() {
        return vision.getAgentId();
    }

    @Override
    public Map<String, Object> getDetails() {
        return Collections.emptyMap();
    }

    @Override
    public Mood tick(LivingAgent me) {
        SortedSet<ScanResult> foundPeers = vision.scan(agent -> agent.getClass().equals(me.getClass()));
        Set<Agent> flockingPeers = foundPeers.stream().map(ScanResult::getAgent).filter(peer -> ((LivingAgent) peer).getMood().getClass().equals(Flocking.class)).collect(Collectors.toSet());
        if (RandomProvider.nextInt(100) < flockingPeers.size() * 10 + BASE_FLOCKING_URGE) {
            return new Flocking(dependencies);
        } else {
            SortedSet<ScanResult> foundEdges = vision.scan(Edge.class::isInstance);
            lastMovementEnergy = locomotion.move(LAND_SPEED_FACTOR, foundEdges);
        }

        return this;
    }

    @Override
    public int getEnergyDelta() {
        return EnergyTracker.ENERGY_DERIVATIVE + (int) Math.round(lastMovementEnergy);
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public String toString() {
        return "Existing2{}";
    }
}
