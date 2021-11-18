package com.github.thehilikus.alife.agent.api.internal;

import com.github.thehilikus.alife.agent.api.LivingAgent;

/**
 * An agent that can transfer its energy to another agent
 */
public interface EatableAgent extends LivingAgent {
    int transferEnergy(int desiredBiteSize);
}
