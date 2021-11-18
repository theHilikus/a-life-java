package com.github.thehilikus.alife.agent.api;

import java.util.Map;

/**
 * A message from one agent to another
 */
public class Message {
    private final Agent.Social sender;
    private final String message;
    private final Map<String, Object> details;

    public Message(Agent.Social sender, String message, Map<String, Object> details) {
        this.sender = sender;
        this.message = message;
        this.details = details;
    }

    public Agent.Social getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
