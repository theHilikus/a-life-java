package com.github.thehilikus.alife.agent.genetics;

import java.util.Map;

/**
 * A set of parameters that control the fitness of an agent
 */
public abstract class Genome {
    private final Map<String, Object> genes;

    protected Genome(Map<String, Object> genes) {
        this.genes = genes;
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
