package com.github.thehilikus.alife.world;

import com.github.thehilikus.alife.agent.api.Agent;

import java.util.Collection;
import java.util.Map;

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

        Collection<? extends Agent> getLivingAgents();

        Collection<? extends Agent> getEdges();

        Map<String, Object> getAgentDetails(int agentId);
    }
}
