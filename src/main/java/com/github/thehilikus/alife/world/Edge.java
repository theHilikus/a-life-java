package com.github.thehilikus.alife.world;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.api.Position;

import java.util.Collections;
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
        return Collections.emptyMap();
    }

    @Override
    public Position.Immutable getPosition() {
        return position;
    }
}
