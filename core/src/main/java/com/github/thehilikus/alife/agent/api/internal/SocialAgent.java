package com.github.thehilikus.alife.agent.api.internal;

/**
 * An agent that can communicate with others
 */
public interface SocialAgent extends EvolvableAgent {
    void communicate(Message message);
}
