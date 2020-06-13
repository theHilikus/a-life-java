package com.github.thehilikus.alife.api;

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
     * @return the number of energy points used in the last turn
     */
    int getEnergyDelta();
}
