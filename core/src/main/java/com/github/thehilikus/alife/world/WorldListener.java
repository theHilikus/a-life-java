package com.github.thehilikus.alife.world;

import com.github.thehilikus.alife.agent.api.AgentDetails;

import java.util.Collection;
import java.util.Optional;

/**
 * Callback to notify of world events
 */
public interface WorldListener {
    boolean ticked(WorldStatus latestStatus);

    void ended(int hour);

    interface WorldStatus {
        int getWidth();

        int getHeight();

        int getAge();

        Collection<AgentDetails.Immutable> getLivingAgentsDetails();

        Collection<AgentDetails.Immutable> getEdges();

        Optional<AgentDetails.Immutable> getAgentDetails(int agentId);
    }
}
