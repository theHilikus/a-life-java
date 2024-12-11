package com.github.thehilikus.alife.agent.motion;

import com.github.thehilikus.alife.agent.api.AgentDetails;
import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;

/**
 * A simple agent for unit tests
 */
public class DummyAgent implements LivingAgent {
    private final Locomotion legs;

    public DummyAgent(Locomotion legs) {
        this.legs = legs;
    }

    @Override
    public int getId() {
        return legs.getAgentId();
    }

    @Override
    public AgentDetails.Immutable getDetails() {
        return null;
    }

    @Override
    public Position.Immutable getPosition() {
        return legs.getPosition();
    }

    @Override
    public int getOrientation() {
        return legs.getOrientation();
    }

    @Override
    public Mood getMood() {
        return null;
    }

    @Override
    public VitalSign tick() {
        return null;
    }
}
