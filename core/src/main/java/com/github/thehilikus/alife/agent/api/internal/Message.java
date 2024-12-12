package com.github.thehilikus.alife.agent.api.internal;

import java.util.Map;

/**
 * A message from one agent to another
 */
public record Message(SocialAgent sender, String message, Map<String, Object> details) {
}
