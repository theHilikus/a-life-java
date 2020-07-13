package com.github.thehilikus.alife.agents.genetics;

import com.github.thehilikus.alife.api.Component;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A set of parameters that control the fitness of an agent
 */
public abstract class Genome implements Component {
    private final int agentId;
    private final Map<String, Object> genes;

    protected Genome(int agentId, Map<String, Object> genes) {
        this.agentId = agentId;
        this.genes = genes;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> mapWithStrings = genes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toString()));
        return Collections.unmodifiableMap(mapWithStrings);
    }

    @SuppressWarnings("unchecked")
    public <T> T getGene(String name) {
        if (!genes.containsKey(name)) {
            throw new IllegalArgumentException("Gene does not exist: " + name);
        }
        return (T) genes.get(name);
    }
}
