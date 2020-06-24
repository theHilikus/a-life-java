package com.github.thehilikus.alife.api;

import com.diogonunes.jcdp.color.api.Ansi;

/**
 * The current behaviour of an agent
 */
public interface Mood extends Component {
    /**
     * The prefix for motion parameters
     */
    String PARAMETER_PREFIX = "mood.";

    Mood tick();

    int getHungerDelta();

    /**
     * @return the number of energy points used (if negative) or gained (if positive) in the last turn
     */
    int getEnergyDelta();

    Ansi.FColor getTerminalColour();
}
