package com.github.thehilikus.alife.agents.animals.moods;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;

/**
 * When an agent has found a potential mate
 */
public class InHeatChasing extends Hunting {
    private static final int PRIORITY = 62;
    private final MoodController moodController;
    private final Agent.Evolvable mate;

    public InHeatChasing(MoodController moodController, Vision vision, Locomotion locomotion, Genome genome, Agent.Evolvable mate) {
        super(moodController, vision, locomotion, genome, mate);
        this.moodController = moodController;
        this.mate = mate;
    }
    @Override
    protected Mood reachedTarget() {
        return moodController.startMating(mate);
    }

    @Override
    public String toString() {
        return "InHeatChasing{" +
                "mate=" + mate.getId() +
                '}';
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
