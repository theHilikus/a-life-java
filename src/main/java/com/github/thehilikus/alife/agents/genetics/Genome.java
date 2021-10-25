package com.github.thehilikus.alife.agents.genetics;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A set of parameters that control the fitness of an agent
 */
public abstract class Genome {
    private final Map<String, Object> genes;

    protected Genome(Map<String, Object> genes) {
        this.genes = genes;
    }

    public Map<String, Object> getParameters() {
        Map<String, Object> mapWithStrings = genes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return Collections.unmodifiableMap(mapWithStrings);
    }

    @SuppressWarnings("unchecked")
    public <T> T getGene(String name) {
        if (!genes.containsKey(name)) {
            throw new IllegalArgumentException("Gene does not exist: " + name);
        }
        return (T) genes.get(name);
    }
    
    public abstract Genome crossover(Genome maternalGenome);

    public abstract void mutate();

    protected Map<String, Object> getGenes() {
        return genes;
    }
}
