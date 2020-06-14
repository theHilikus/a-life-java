package com.github.thehilikus.alife.agents.modules;

import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.ScanResult;
import com.github.thehilikus.alife.api.Vision;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;

/**
 * A vision that doesn't see anything
 */
public class NoVision implements Vision {
    private final int agentId;

    private NoVision(int id) {
        agentId = id;
    }

    public static Vision create(int id) {
        return new NoVision(id);
    }

    @Override
    public <T extends Agent> SortedSet<ScanResult> scan(Class<T> type) {
        return Collections.emptySortedSet();
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Collections.emptyMap();
    }
}
