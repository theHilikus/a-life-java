package com.github.thehilikus.alife.agents.animals.moods;

import com.github.thehilikus.alife.agents.controllers.AgeTracker;
import com.github.thehilikus.alife.agents.controllers.SizeTracker;
import com.github.thehilikus.alife.agents.controllers.SocialController;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Message;
import com.github.thehilikus.alife.api.Mood;

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
