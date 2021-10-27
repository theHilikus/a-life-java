package com.github.thehilikus.alife.agents.animals.moods;

import com.github.thehilikus.alife.agents.animals.motions.Legs;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.MoodController;
import com.github.thehilikus.alife.api.Vision;

/**
 * When an agent has found a potential mate
 */
public class InHeatChasing extends Hunting {

    private final MoodController moodController;
    private final Agent.Evolvable mate;

    public InHeatChasing(MoodController moodController, Vision vision, Legs legs, Genome genome, Agent.Evolvable mate) {
        super(moodController, vision, legs, genome, mate);
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
}
