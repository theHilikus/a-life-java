package com.github.thehilikus.alife.agents.animals;

import com.github.thehilikus.alife.agents.animals.moods.Existing;
import com.github.thehilikus.alife.agents.animals.motions.Legs;
import com.github.thehilikus.alife.agents.animals.motions.StraightWalkWithRandomTurn;
import com.github.thehilikus.alife.agents.animals.visions.SurroundingsVision;
import com.github.thehilikus.alife.agents.controllers.*;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.World;
import dagger.Module;
import dagger.Provides;

/**
 * Dependencies provider for Herbivores
 */
@Module
public class HerbivoreModule {
    @Provides
    @AgentScope
    Vision provideVision(int agentId, Genome genome, World world) {
        return new SurroundingsVision(agentId, genome, world);
    }

    @Provides
    @AgentScope
    Locomotion provideLocomotion(int agentId, Position startingPosition, Genome genome) {
        return new StraightWalkWithRandomTurn(agentId, startingPosition, genome);
    }

    @Provides
    @AgentScope
    Legs provideLegs(int agentId, Position position, Genome genome) {
        return new Legs(agentId, position, genome);
    }

    @Provides
    @AgentScope
    Mood provideStartingMood(Vision vision, Genome genome, Locomotion locomotion) {
        return new Existing(vision, genome, locomotion);
    }

    @Provides
    @AgentScope
    MoodController provideMoodController(Vision vision, Legs legs, Locomotion locomotion, Genome genome, HungerTracker hungerTracker, EnergyTracker energyTracker, AgeTracker ageTracker, PregnancyTracker pregnancyTracker, World world) {
        return new HerbivoreMoodController(vision, legs, locomotion, genome, hungerTracker, energyTracker, ageTracker, pregnancyTracker, world);
    }
}
