package com.github.thehilikus.alife.agent.api;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A group of properties that describe an agent
 */
public class AgentDetails {
    private final Map<String, Object> details = new LinkedHashMap<>();

    public AgentDetails(int id, String type, Position.Immutable position) {
        details.put("id", id);
        details.put("type", type);
        details.put("position", position);
    }

    public void addAttribute(String name, Object value) {
        details.put(name, value);
    }

    public void addAllDetails(Map<String, Object> otherDetails) {
        details.putAll(otherDetails);
    }

    public AgentDetails.Immutable toImmutable() {
        return new Immutable();
    }

    public class Immutable {
        public int getId() {
            return (int) details.get("id");
        }

        public String getType() {
            return details.get("type").toString();
        }

        public Position.Immutable getPosition() {
            return (Position.Immutable) details.get("position");
        }

        @SuppressWarnings("unchecked")
        public <T> T getAttribute(String name) {
            return (T) details.get(name);
        }

        public void forEach(BiConsumer<String, Object> action) {
            details.forEach(action);
        }

        public Iterable<? extends Map.Entry<String, Object>> entrySet() {
            return details.entrySet();
        }
    }
}
