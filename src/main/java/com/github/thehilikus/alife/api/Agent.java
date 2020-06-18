package com.github.thehilikus.alife.api;

import com.github.thehilikus.alife.agents.genetics.Genome;

import java.util.Map;

/**
 * An entity in the World
 */
public interface Agent {
    int getId();

    Map<String, String> getDetails();

    void tick();

    /**
     * returns a coordinate with the current position of the agent
     */
    Position getPosition();

    /**
     * A textual representation of the agent with its id. The style represents the type of agent, the colour
     * represents its current mood
     */
    String getStringRepresentation();

    Genome getGenome();
}
