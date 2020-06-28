package com.github.thehilikus.alife.agents.plants;

import com.github.thehilikus.alife.agents.plants.moods.Growing;
import com.github.thehilikus.alife.api.AgentScope;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.Position;
import com.github.thehilikus.alife.world.World;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

/**
 * Dependencies provider for {@link Plant}
 */
@Module
public class PlantModule {
    @Provides
    @AgentScope
    static Position provideEmptyPosition(World world) {
        return world.getEmptyPosition();
    }

    @Provides
    @AgentScope
    @Named("plants")
    static Mood provideStartingMood(int agentId) {
        return new Growing(agentId);
    }
}
