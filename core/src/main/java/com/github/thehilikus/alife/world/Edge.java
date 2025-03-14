package com.github.thehilikus.alife.world;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.api.AgentDetails;
import com.github.thehilikus.alife.agent.api.Position;

/**
 * An agent that represents a side limit of the world
 */
public record Edge(Position.Immutable position) implements Agent {

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public AgentDetails.Immutable getDetails() {
        AgentDetails details = new AgentDetails(getId(), getClass().getSimpleName(), position);

        return details.toImmutable();
    }

    @Override
    public String toString() {
        return "Edge{" +
                "position=" + position +
                '}';
    }
}
