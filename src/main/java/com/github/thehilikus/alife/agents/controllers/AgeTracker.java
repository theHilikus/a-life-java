package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.api.Component;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

/**
 * Monitors the age of an agent
 */
public class AgeTracker implements VitalSign, Component {
    private final int agentId;
    private final int lifeExpectancy;
    @PositiveOrZero
    private int currentAge;

    public AgeTracker(int agentId, int lifeExpectancy) {
        this.agentId = agentId;
        this.lifeExpectancy = lifeExpectancy;
    }

    @Override
    public void update(Mood currentMood) {
        currentAge++;
    }

    @Override
    public boolean isAlive() {
        return currentAge <= lifeExpectancy;
    }

    @Override
    public int getValue() {
        return currentAge;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "age", Integer.toString(currentAge),
                PARAMETER_PREFIX + "lifeExpectancy", Integer.toString(lifeExpectancy)
        );
    }
}
