package com.github.thehilikus.alife.api;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

/**
 * A part of an agent
 */
public interface Component {
    @PositiveOrZero
    int getAgentId();

    @NotNull
    Map<String, String> getParameters();
}
