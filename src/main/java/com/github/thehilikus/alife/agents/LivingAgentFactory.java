package com.github.thehilikus.alife.agents;

import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.world.World;

/**
 * Base class to constructing any type of agent
 */
public abstract class LivingAgentFactory {
    private static World world;

    public void createAgents(int count) {
        for (int pos = 0; pos < count; pos++) {
            createAgent();
        }
    }

    protected static World getWorld() {
        return world;
    }

    public static void setWorld(World world) {
        LivingAgentFactory.world = world;
    }

    protected abstract Agent.Living createAgent();
}
