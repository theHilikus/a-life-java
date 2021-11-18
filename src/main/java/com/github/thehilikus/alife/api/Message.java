package com.github.thehilikus.alife.api;

import java.util.Map;

/**
 * A message from one agent to another
 */
public class Message {
    private final Agent.Social sender;
    private final String message;
    private final Map<String, Object> senderDetails;

    public Message(Agent.Social sender, String message, Map<String, Object> senderDetails) {
        this.sender = sender;
        this.message = message;
        this.senderDetails = senderDetails;
    }

    public Agent.Social getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getSenderDetails() {
        return senderDetails;
    }
}
