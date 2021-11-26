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
    protected Genome create(Map<String, Object> genes) {
        return null;
    }

    @Override
    protected Map<String, Object> createGenes() {
        return null;
    }
}
