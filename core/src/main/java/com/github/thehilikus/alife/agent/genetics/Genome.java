package com.github.thehilikus.alife.agent.genetics;

import com.github.thehilikus.alife.agent.api.RandomProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A set of parameters that control the fitness of an agent
 */
public abstract class Genome {
    private static final Logger LOG = LoggerFactory.getLogger(Genome.class);
    private static final double CROSSOVER_PROBABILITY = 0.7;
    private static final double MUTATION_PROBABILITY = 0.05;
    protected static final int MIN_VISION_DISTANCE = 5;

    private final Map<String, Object> genes;

    protected Genome() {
        this.genes = createGenes();
    }

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

    private Map<String, Object> getGenes() {
        return genes;
    }

    public Genome crossover(Genome maternalGenome) {
        double random = RandomProvider.nextDouble(1);
        Genome result;
        if (random < CROSSOVER_PROBABILITY) {
            result = singlePointCrossover(maternalGenome);
        } else {
            boolean cloneMother = RandomProvider.nextBoolean();
            if (cloneMother) {
                result = create(new HashMap<>(maternalGenome.getGenes()));
            } else {
                result = create(new HashMap<>(getGenes()));
            }
            LOG.debug("No crossover. Instead, cloning the genome of the " + (cloneMother ? "mother" : "father"));
        }

        return result;
    }

    private Genome singlePointCrossover(Genome maternalGenome) {
        Iterator<Map.Entry<String, Object>> paternalGenesIter = getGenes().entrySet().iterator();
        Iterator<Map.Entry<String, Object>> maternalGenesIter = maternalGenome.getGenes().entrySet().iterator();

        Map<String, Object> offspringGenes = new HashMap<>();
        int splitPoint = RandomProvider.nextInt(1, getGenes().size());
        LOG.debug("Performing single-point crossover with split point = {}", splitPoint);
        for (int pos = 0; pos < getGenes().size(); pos++) {
            Map.Entry<String, Object> paternalGene = paternalGenesIter.next();
            Map.Entry<String, Object> maternalGene = maternalGenesIter.next();
            if (!paternalGene.getKey().equals(maternalGene.getKey())) {
                throw new IllegalStateException("Genes are not properly aligned");
            }
            if (pos < splitPoint) {
                offspringGenes.put(maternalGene.getKey(), maternalGene.getValue());
            } else {
                offspringGenes.put(paternalGene.getKey(), paternalGene.getValue());
            }
        }

        return create(offspringGenes);
    }

    public void mutate() {
        Map<String, Object> mutationsPool = createGenes();

        Iterator<Map.Entry<String, Object>> currentGenesIter = getGenes().entrySet().iterator();
        Iterator<Map.Entry<String, Object>> mutationsPoolIter = mutationsPool.entrySet().iterator();

        for (int pos = 0; pos < getGenes().size(); pos++) {
            Map.Entry<String, Object> currentGene = currentGenesIter.next();
            Map.Entry<String, Object> mutatedGene = mutationsPoolIter.next();
            if (!currentGene.getKey().equals(mutatedGene.getKey())) {
                throw new IllegalStateException("Genes are not properly aligned");
            }
            if (RandomProvider.nextDouble(1) < MUTATION_PROBABILITY) {
                getGenes().put(mutatedGene.getKey(), mutatedGene.getValue());
            }
        }
    }

    protected abstract Genome create(Map<String, Object> genes);

    protected abstract Map<String, Object> createGenes();
}
