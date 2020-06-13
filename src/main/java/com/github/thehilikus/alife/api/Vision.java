package com.github.thehilikus.alife.api;

import java.util.SortedSet;

/**
 * A strategy to see in the world
 */
public interface Vision extends Component {
    /**
     * The prefix for vision parameters
     */
    String PARAMETER_PREFIX = "vision.";

    /**
     * Searches for agents around itself
     *
     * @param type the type of agent to scan for
     * @param <T>  one of the agents
     * @return a sorted list with the closest agent first in the set
     */
    <T extends Agent> SortedSet<ScanResult> scan(Class<T> type);
}
