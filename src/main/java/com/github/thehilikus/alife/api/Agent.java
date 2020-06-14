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

    /**
     * A textual representation of the agent in the following format:
     * <p>
     * {@code
     * id{2}mood{1}
     * }
     * </p>
     * Example:
     * If agent 3 is Hunting, its representation can be "03H"
     *
     * @return a string representing the agent in 3 characters
     */
    String getStringRepresentation();

    Motion getMotion();

    Vision getVision();

    Object getMood();
}
