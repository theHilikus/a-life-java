package com.github.thehilikus.alife.agents.animals.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.controllers.PregnancyTracker;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedSet;

public class Mating implements Mood {
    private static final Logger LOG = LoggerFactory.getLogger(Mating.class.getSimpleName());
    private static final double MATE_ENERGY_FACTOR = 1.25;
    private final MoodController moodController;
    private final Genome genome;
    private final PregnancyTracker pregnancyTracker;
    private final Agent.Evolvable mate;
    private final Vision vision;
    private final int matingDuration;
    private final World world;
    private int matingEnergySpent;
    private int timeWithMate;

    public Mating(MoodController moodController, Vision vision, Genome genome, PregnancyTracker pregnancyTracker, Agent.Evolvable mate, World world) {
        this.moodController = moodController;
        this.vision = vision;
        this.genome = genome;
        this.pregnancyTracker = pregnancyTracker;
        this.mate = mate;
        this.matingDuration = genome.getGene(Agent.Evolvable.PARAMETER_PREFIX + "matingDuration");
        this.world = world;
    }


    @Override
    public Mood tick() {
        SortedSet<ScanResult> mateScans = vision.scan(agent -> agent == mate);
        if (!mateScans.isEmpty()) {
            ScanResult mateScan = mateScans.first();
            if (Math.abs(mateScan.getXDistance()) <= 1 && Math.abs(mateScan.getYDistance()) <= 1) {
                LOG.debug("Mating with {}: {}/{}", mate, timeWithMate, matingDuration);
                if (timeWithMate >= matingDuration) {
                    LOG.info("Giving birth");
                    Genome offspringGenome = genome;
                    OffspringComponent combinerComponent = DaggerOffspringComponent.builder()
                            .offspringGenome(offspringGenome)
                            .world(world)
                            .build();
                    Agent.Evolvable baby = combinerComponent.createOffspring();
                    pregnancyTracker.giveBirth(baby);

                    return moodController.startIdling();
                }
                timeWithMate++;
                matingEnergySpent += Math.round(EnergyTracker.ENERGY_DERIVATIVE * MATE_ENERGY_FACTOR);
            } else {
                timeWithMate = 0;
                LOG.debug("Mate {} is too far", mate);
                return moodController.startFollowing(mate);
            }
        } else {
            LOG.debug("Mate {} is gone :'(", mate.getId());
            timeWithMate = 0;
            return moodController.startScouting();
        }

        return this;
    }

    @Override
    public int getEnergyDelta() {
        return EnergyTracker.ENERGY_DERIVATIVE + matingEnergySpent;
    }

    @Override
    public Ansi.FColor getTerminalColour() {
        return Ansi.FColor.RED;
    }

    @Override
    public int getAgentId() {
        return vision.getAgentId();
    }

    @Override
    public Map<String, String> getParameters() {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    @Override
    public String toString() {
        return "Mating{" +
                "mate=" + mate.getId() +
                '}';
    }
}
