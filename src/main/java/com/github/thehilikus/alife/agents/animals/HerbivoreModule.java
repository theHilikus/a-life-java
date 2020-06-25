package com.github.thehilikus.alife.agents.animals;

import com.github.thehilikus.alife.agents.animals.moods.Existing;
import com.github.thehilikus.alife.agents.animals.motions.StraightWalkWithRandomTurn;
import com.github.thehilikus.alife.agents.animals.visions.SurroundingsVision;
import com.github.thehilikus.alife.agents.controllers.HerbivoreMoodController;
import com.github.thehilikus.alife.agents.controllers.VitalsController;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.World;
import dagger.Module;
import dagger.Provides;

/**
 * Dependencies creator for Herbivores
 */
@Module
public class HerbivoreModule {
    @Provides
    @AgentScope
    static Position providePosition(World world) {
        return world.getEmptyPosition();
    }

    @Provides
    @AgentScope
    static Vision provideVision(int agentId, Genome genome, World world) {
        return new SurroundingsVision(agentId, genome, world);
    }

    @Provides
    @AgentScope
    static Locomotion provideLocomotion(int agentId, Position startingPosition, Genome genome) {
        return new StraightWalkWithRandomTurn(agentId, startingPosition, genome);
    }

    @Provides
    @AgentScope
    static MoodController provideMoodController(int agentId, World world) {
        return new HerbivoreMoodController(agentId, world);
    }

    @Provides
    @AgentScope
    static Genome provideGenome(int agentId) {
        return new Herbivore.HerbivoreGenome(agentId);
    }

    @Provides
    @AgentScope
    static Mood provideStartingMood(Vision vision, Genome genome, Locomotion locomotion) {
        return new Existing(vision, genome, locomotion);
    }

    @Provides
    @AgentScope
    static VitalsController provideVitals(int agentId, MoodController moodController, Genome genome) {
        return new VitalsController(agentId, moodController, genome);
    }
}
