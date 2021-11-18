package com.github.thehilikus.alife.agent.api.internal;

import java.util.Map;

/**
 * A message from one agent to another
 */
public class Message {
    private final SocialAgent sender;
    private final String message;
    private final Map<String, Object> details;

    public Message(SocialAgent sender, String message, Map<String, Object> details) {
        this.sender = sender;
        this.message = message;
        this.details = details;
    }

    public SocialAgent getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
