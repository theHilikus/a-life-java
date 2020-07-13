package com.github.thehilikus.alife.agents.animals;

import com.github.thehilikus.alife.api.AgentScope;
import com.github.thehilikus.alife.api.Position;
import com.github.thehilikus.alife.world.World;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for agents that have parents
 */
@Module
public class OffspringModule {
    @Provides
    @AgentScope
    Position provideEmptyPosition(World world) {
        //TODO: return a position close to the parent
        return world.getEmptyPosition();
    }
}
