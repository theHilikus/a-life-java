package com.github.thehilikus.alife.agents.plants;

import com.github.thehilikus.alife.api.AgentScope;
import com.github.thehilikus.alife.api.Position;
import com.github.thehilikus.alife.world.World;
import dagger.Module;
import dagger.Provides;

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
}
