package com.github.thehilikus.alife.agents.animals.moods;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.animals.Herbivore;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.SortedSet;

/**
 * When an agent is looking for a suitable mate to reproduce
 */
public class InHeat implements Mood {

    private final Existing existing;
    private final MoodController moodController;
    private final Vision vision;

    public InHeat(MoodController moodController, Vision vision, Genome genome, Locomotion locomotion) {
        this.existing = new Existing(vision, genome, locomotion);
        this.moodController = moodController;
        this.vision = vision;
    }

    @Override
    public @NotNull Mood tick() {
        SortedSet<ScanResult> potentialMates = vision.scan(Herbivore.class::isInstance);
        if (!potentialMates.isEmpty()) {
            Agent closestMate = potentialMates.first().getAgent();
            return moodController.startFollowing((Agent.Evolvable) closestMate);
        } else {
            existing.tick();
            return this;
        }
    }

    @Override
    public int getEnergyDelta() {
        return existing.getEnergyDelta();
    }

    @Override
    public Ansi.FColor getTerminalColour() {
        return existing.getTerminalColour();
    }

    @Override
    public int getAgentId() {
        return existing.getAgentId();
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(PARAMETER_PREFIX + "current", getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return "InHeat{}";
    }
}
