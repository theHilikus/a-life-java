package com.github.thehilikus.alife.agent.moods;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.api.Message;
import com.github.thehilikus.alife.agent.controllers.SocialController;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.vitals.AgeTracker;
import com.github.thehilikus.alife.agent.vitals.AgentModules;
import com.github.thehilikus.alife.agent.vitals.SizeTracker;

import java.util.HashMap;
import java.util.Map;

/**
 * When an agent has found a potential mate
 */
public class InHeatChasing extends Hunting {
    private static final int PRIORITY = 62;
    private final AgeTracker ageTracker;
    private final SizeTracker sizeTracker;
    private final Agent.Social mate;
    private final AgentModules dependencies;

    public InHeatChasing(AgentModules dependencies, Agent.Social mate) {
        super(dependencies, mate);
        this.dependencies = dependencies;
        this.ageTracker = dependencies.getAgeTracker();
        this.sizeTracker = dependencies.getSizeTracker();
        this.mate = mate;
    }

    @Override
    protected Mood reachedTarget(Agent.Social me) {
        Map<String, Object> details = new HashMap<>();
        details.putAll(ageTracker.getDetails());
        details.putAll(sizeTracker.getDetails());
        Message message = new Message(me, SocialController.MessageType.MATING_CALL, details);
        mate.communicate(message);

        return new Mating(dependencies, mate);
    }

    @Override
    public String toString() {
        return "InHeatChasing{" +
                "mate=" + mate.getId() +
                '}';
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
