package com.github.thehilikus.alife.world;

import com.github.thehilikus.alife.agent.api.AgentDetails;

import java.util.Collection;
import java.util.Optional;

/**
 * Callback to notify of world events
 */
public interface WorldListener {
    /**
     * Called after every tick of the clock
     *
     * @param latestStatus the current status of the world after the tick completed
     * @return true if the simulation should continue
     */
    boolean ticked(WorldStatus latestStatus);

    /**
     * Called when the simulation has ended
     *
     * @param hour the age of the world when the simulation ended
     */
    void ended(int hour);

    /**
     * Read-only interface to the status of the world
     */
    interface WorldStatus {
        int getWidth();

        int getHeight();

        int getAge();

        Collection<AgentDetails.Immutable> getLivingAgentsDetails();

        Collection<AgentDetails.Immutable> getEdges();

        Optional<AgentDetails.Immutable> getAgentDetails(int agentId);
    }
}
