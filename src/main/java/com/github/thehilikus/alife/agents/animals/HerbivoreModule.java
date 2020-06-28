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
    static Legs provideLegs(int agentId, Position position, Genome genome) {
        return new Legs(agentId, position, genome);
    }

    @Provides
    @AgentScope
    static MoodController provideMoodController(Vision vision, Legs legs, Locomotion locomotion, Genome genome, HungerTracker hungerTracker, EnergyTracker energyTracker) {
        return new HerbivoreMoodController(vision, legs, locomotion, genome, hungerTracker, energyTracker);
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
    static VitalsController provideVitals(int agentId, MoodController moodController, HungerTracker hungerTracker, EnergyTracker energyTracker, AgeTracker ageTracker) {
        return new VitalsController(agentId, moodController, hungerTracker, energyTracker, ageTracker);
    }

    @Provides
    @AgentScope
    static HungerTracker provideHungerTracker(int agentId, Genome genome) {
        return new HungerTracker(agentId, genome.getGene(VitalSign.PARAMETER_PREFIX + "hungryThreshold"));
    }

    @Provides
    @AgentScope
    static EnergyTracker provideEnergyTracker(int agentId, Genome genome) {
        return new EnergyTracker(agentId, genome.getGene(VitalSign.PARAMETER_PREFIX + "lowEnergyThreshold"));
    }

    @Provides
    @AgentScope
    static AgeTracker provideAgeTracker(int agentId, Genome genome) {
        return new AgeTracker(agentId, genome.getGene(VitalSign.PARAMETER_PREFIX + "lifeExpectancy"));
    }
}
