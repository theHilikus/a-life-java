package com.github.thehilikus.alife.agent.genetics;

import java.util.Map;

/**
 * Genome to use in unit testing
 */
public class DummyGenome extends Genome {

    public DummyGenome(Map<String, Object> genes) {
        super(genes);
    }

    @Override
    public Genome crossover(Genome maternalGenome) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void mutate() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
