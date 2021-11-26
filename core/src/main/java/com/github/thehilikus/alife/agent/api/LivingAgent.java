package com.github.thehilikus.alife.agent.api;

import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;

/**
 * An agent that does actions in every turn
 */
public interface LivingAgent extends Agent {
    /**
     * Execute one turn of the agent
     *
     * @return null if the agent is alive at the end of the turn or the vital sign that caused the death
     */
    VitalSign tick();

    default int getOrientation() {
        return 0;
    }

    Mood getMood();

    /**
     * Min size for any agent
     */
    int MIN_SIZE = 6;
}
