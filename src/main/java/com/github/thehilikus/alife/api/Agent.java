package com.github.thehilikus.alife.api;

import java.util.Map;

/**
 * An entity in the World
 */
public interface Agent {
    int getId();

    Map<String, String> getParameters();
    void tick();

    /**
     * returns an immutable coordinate with the current position of the agent
     */
    Coordinates.Immutable getPosition();
}
