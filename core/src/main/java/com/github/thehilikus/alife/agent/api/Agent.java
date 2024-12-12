package com.github.thehilikus.alife.agent.api;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * An entity in the World
 */
public interface Agent {
    @PositiveOrZero
    int getId();

    @NotNull
    AgentDetails.Immutable getDetails();

    Position.Immutable position();

}
