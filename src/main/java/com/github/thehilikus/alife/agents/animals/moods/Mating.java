package com.github.thehilikus.alife.agents.animals.moods;

import com.github.thehilikus.alife.agents.animals.HerbivoreFactory;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.controllers.ReproductionTracker;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedSet;

/**
 * When an agent is reproducing
 */
public class Mating implements Mood {
    private static final int PRIORITY = 65;
    private static final Logger LOG = LoggerFactory.getLogger(Mating.class.getSimpleName());
    private static final double MATE_ENERGY_FACTOR = 1.25;
    private final Genome genome;
    private final ReproductionTracker reproductionTracker;
    private final Agent.Evolvable mate;
    private final Vision vision;
    private final int matingDuration;
    private final AgentModules dependencies;
    private int matingEnergySpent;
    private int timeWithMate;

    public Mating(MoodController moodController, Vision vision, Genome genome, ReproductionTracker reproductionTracker, Agent.Evolvable mate) {
        this.moodController = moodController;
        this.vision = vision;
        this.genome = genome;
        this.reproductionTracker = reproductionTracker;
        this.mate = mate;
        this.matingDuration = genome.getGene(Agent.Evolvable.PARAMETER_PREFIX + "matingDuration");
    }


    @Override
    public Mood tick(Agent.Living me) {
        SortedSet<ScanResult> mateScans = vision.scan(agent -> agent == mate);
        if (!mateScans.isEmpty()) {
            ScanResult mateScan = mateScans.first();
            if (mateScan.getDistanceSquared() < 4) {
                LOG.debug("Mating with {}: {}/{}", mate, timeWithMate, matingDuration);
                if (timeWithMate >= matingDuration) {
                    LOG.info("Giving birth");

                    Agent.Living offspring = new HerbivoreFactory().createOffspring(vision.getAgentId(), genome, mate.getGenome());
                    reproductionTracker.gaveBirth(mate.getId(), offspring.getId());

                    return new Existing(dependencies);
                }
                timeWithMate++;
                matingEnergySpent += Math.round(EnergyTracker.ENERGY_DERIVATIVE * MATE_ENERGY_FACTOR);
            } else {
                timeWithMate = 0;
                LOG.debug("Mate {} is too far", mate);
                return new InHeatChasing(dependencies, mate);
            }
        } else {
            LOG.debug("Mate {} is gone :'(", mate.getId());
            timeWithMate = 0;
            return new Scouting(dependencies);
        }

        return this;
    }

    @Override
    public int getEnergyDelta() {
        return EnergyTracker.ENERGY_DERIVATIVE + matingEnergySpent;
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public int getAgentId() {
        return vision.getAgentId();
    }

    @Override
    public Map<String, Object> getParameters() {
        return Map.of(
                PARAMETER_PREFIX + "matingDuration", matingDuration,
                PARAMETER_PREFIX + "mate", mate.getId(),
                PARAMETER_PREFIX + "timeWithMate", timeWithMate);
    }

    @Override
    public String toString() {
        return "Mating{" +
                "mate=" + mate.getId() +
                '}';
    }
}
