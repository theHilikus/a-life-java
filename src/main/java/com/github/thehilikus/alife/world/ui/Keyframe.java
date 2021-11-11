package com.github.thehilikus.alife.world.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * An animation frame of the full state of the world
 */
public class Keyframe implements Iterable<AgentKeyframe> {
    private final int worldAge;
    private final Collection<AgentKeyframe> agentFrames = new TreeSet<>();

    public Keyframe(int worldAge) {
        this.worldAge = worldAge;
    }

    public void addAgentFrame(AgentKeyframe agentFrame) {
        agentFrames.add(agentFrame);
    }

    @Override
    public Iterator<AgentKeyframe> iterator() {
        return agentFrames.iterator();
    }

    public int getWorldAge() {
        return worldAge;
    }

    public AgentKeyframe getAgentKeyframe(int agentId) {
        for (AgentKeyframe agentKeyframe : agentFrames) {
            if (agentKeyframe.getAgentId() == agentId) {
                return agentKeyframe;
            }
        }

        return null;
    }
}
