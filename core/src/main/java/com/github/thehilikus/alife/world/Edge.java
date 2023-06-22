package com.github.thehilikus.alife.world;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.api.Position;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An agent that represents a side limit of the world
 */
public class Edge implements Agent {
    private final Position.Immutable position;

    public Edge(Position.Immutable position) {
        this.position = position;
    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public Map<String, Object> getDetails() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", getClass().getSimpleName());
        result.put("position", position);

        return result;
    }

    @Override
    public Position.Immutable getPosition() {
        return position;
    }
}
