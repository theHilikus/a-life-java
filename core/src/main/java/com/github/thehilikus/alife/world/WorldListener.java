package com.github.thehilikus.alife.world;

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

        Collection<Map<String, Object>> getLivingAgentsDetails();

        Collection<Map<String, Object>> getEdges();

        Map<String, Object> getAgentDetails(int agentId);
    }
}
