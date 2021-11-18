package com.github.thehilikus.alife.agent.api;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

/**
 * An entity in the World
 */
public interface Agent {
    @PositiveOrZero
    int getId();

    @NotNull
    Map<String, Object> getDetails();

    Position.Immutable getPosition();

}
