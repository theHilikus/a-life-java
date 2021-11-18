package com.github.thehilikus.alife.agent.controllers;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.api.Component;
import com.github.thehilikus.alife.agent.api.Message;
import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.moods.Mating;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.vitals.AgentModules;
import com.github.thehilikus.alife.agent.vitals.ReproductionTracker;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * In charge of social interactions with other agents
 */
public class SocialController implements Component {
    private static final Logger LOG = LoggerFactory.getLogger(SocialController.class.getSimpleName());
    private final int agentId;
    private final AgentModules dependencies;
    private Mood socialMood;
    private final VitalsController vitals;
    private final ReproductionTracker reproduction;

    public SocialController(int agentId, AgentModules dependencies, VitalsController vitals) {
        this.agentId = agentId;
        this.dependencies = dependencies;
        this.vitals = vitals;
        this.reproduction = dependencies.getReproductionTracker();
    }

    public Mood nextMood(Mood currentMood) {
        Mood result = Objects.requireNonNullElse(socialMood, currentMood);
        socialMood = null;

        return result;
    }

    public void receiveMessage(Message message) {
        LOG.debug("Agent {} received message {}", agentId, message);
        if (message.getMessage().equals(MessageType.MATING_CALL)) {
            mateRequest(message.getSender(), message.getDetails());
        } else if (message.getMessage().equals(MessageType.NEW_OFFSPRING)) {
            newOffspring(message);
        }
    }

    private void mateRequest(Agent.Social sender, Map<String, Object> senderDetails) {
        double senderFitness = calculateFitness(vitals.getDetails(), senderDetails);
        final double fitnessLeeway = 0.20;
        if (reproduction.isWombRested() && RandomProvider.nextDouble(1.0) < senderFitness + fitnessLeeway) {
            Mood newMood = new Mating(dependencies, sender);
            changeMoodFromExternal(newMood);
        }
    }

    private double calculateFitness(Map<String, Object> myDetails, Map<String, Object> theirDetails) {
        double sizeFitness = (double) (int) theirDetails.get("size") / (int) myDetails.get(VitalSign.PARAMETER_PREFIX + "maxSize");
        double ageFitness = (double) (int) theirDetails.get(VitalSign.PARAMETER_PREFIX + "age") / (int) myDetails.get(VitalSign.PARAMETER_PREFIX + "lifeExpectancy");

        return (sizeFitness + ageFitness) / 2;
    }

    private void changeMoodFromExternal(Mood newMood) {
        if (socialMood == null || newMood.getPriority() > socialMood.getPriority()) {
            socialMood = newMood;
        }
    }

    private void newOffspring(Message message) {
        int offspringId = (int) message.getDetails().get("offspringId");
        int motherId = message.getSender().getId();
        reproduction.gaveBirth(motherId, offspringId);
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, Object> getDetails() {
        return null;
    }

    public static class MessageType {
        /**
         * the agent has a new offspring birthed by another agent
         */
        public static final String NEW_OFFSPRING = "you have a baby";
        /**
         * the agent is trying to convince another agent to mate
         */
        public static final String MATING_CALL = "how you doin";
    }
}
