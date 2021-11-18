package com.github.thehilikus.alife.agent.moods.api;

import com.github.thehilikus.alife.agent.api.internal.Component;
import com.github.thehilikus.alife.agent.api.LivingAgent;

import javax.validation.constraints.NotNull;

/**
 * The current behaviour of an agent
 */
public interface Mood extends Component {
    /**
     * The prefix for motion parameters
     */
    String PARAMETER_PREFIX = "mood.";

    /**
     * Executes the current mood
     *
     * @param me a reference to the agent that's being acted on
     * @return the mood for the next hour
     */
    @NotNull
    Mood tick(LivingAgent me);

    default int getHungerDelta() {
        return HUNGER_DERIVATIVE;
    }

    /**
     * The number of hunger points to lose per round
     */
    int HUNGER_DERIVATIVE = -2;

    /**
     * @return the number of energy points used (if negative) or gained (if positive) in the last turn
     */
    int getEnergyDelta();

    int getPriority();
}
