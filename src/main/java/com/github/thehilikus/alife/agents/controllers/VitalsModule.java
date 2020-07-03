package com.github.thehilikus.alife.agents.controllers;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.AgentScope;
import com.github.thehilikus.alife.api.MoodController;
import com.github.thehilikus.alife.api.VitalSign;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module to create vitals dependencies
 */
@Module
public class VitalsModule {
    @Provides
    @AgentScope
    public static VitalsController provideVitals(int agentId, MoodController moodController, HungerTracker hungerTracker, EnergyTracker energyTracker, AgeTracker ageTracker) {
        return new VitalsController(agentId, moodController, hungerTracker, energyTracker, ageTracker);
    }

    @Provides
    @AgentScope
    public static HungerTracker provideHungerTracker(Genome genome) {
        return new HungerTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "hungryThreshold"));
    }

    @Provides
    @AgentScope
    public static EnergyTracker provideEnergyTracker(int agentId, Genome genome) {
        return new EnergyTracker(agentId, genome.getGene(VitalSign.PARAMETER_PREFIX + "lowEnergyThreshold"));
    }

    @Provides
    @AgentScope
    public static AgeTracker provideAgeTracker(Genome genome) {
        return new AgeTracker(genome.getGene(VitalSign.PARAMETER_PREFIX + "lifeExpectancy"));
    }
}
