package com.github.thehilikus.alife.agent.api.internal;

import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.genetics.Genome;

import javax.validation.constraints.NotNull;

/**
 * An agent that has a genotype
 */
public interface EvolvableAgent extends LivingAgent {
    @NotNull
    Genome getGenome();
}
