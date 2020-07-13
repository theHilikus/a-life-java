package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;
import com.github.thehilikus.alife.world.World;

/**
 * Records information about an agent's pregnancy
 */
public class PregnancyTracker implements VitalSign {
    private final World world;
    private int timeSinceBirth;

    public PregnancyTracker(World world) {
        this.world = world;
    }

    @Override
    public void update(Mood currentMood) {
        timeSinceBirth++;
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public int getValue() {
        return timeSinceBirth;
    }

    public void giveBirth(Agent.Evolvable baby) {
        world.addAgent(baby);
    }
}
