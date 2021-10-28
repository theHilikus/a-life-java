package com.github.thehilikus.alife.api;

import javax.validation.constraints.NotNull;

/**
 * The current behaviour of an agent
 */
public interface Mood extends Component {
    /**
     * The prefix for motion parameters
     */
    String PARAMETER_PREFIX = "mood.";

    @NotNull
    Mood tick();

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
