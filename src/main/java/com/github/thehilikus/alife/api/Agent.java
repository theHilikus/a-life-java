package com.github.thehilikus.alife.api;

import com.github.thehilikus.alife.agents.genetics.Genome;

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
    Map<String, String> getDetails();

    /**
     * Execute one turn of the agent
     *
     * @return true if the agent is alive at the end of the turn
     */
    boolean tick();

    /**
     * returns a coordinate with the current position of the agent
     */
    default Position.Immutable getPosition() {
        return getMovablePosition().toImmutable();
    }

    @NotNull
    Position getMovablePosition();

    /**
     * A textual representation of the agent with its id. The style represents the type of agent, the colour
     * represents its current mood
     */
    String getStringRepresentation();

    @NotNull
    Genome getGenome();
}
