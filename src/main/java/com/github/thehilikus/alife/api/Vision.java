package com.github.thehilikus.alife.api;

import javax.validation.constraints.NotNull;
import java.util.SortedSet;
import java.util.function.Predicate;

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
     * @param test A test condition to declare what the vision can detect
     * @return A sorted list with the most interesting agent first in the set. The concept of "most interesting" is implementation-dependent
     */
    @NotNull
    SortedSet<ScanResult> scan(Predicate<Agent> test);
}
