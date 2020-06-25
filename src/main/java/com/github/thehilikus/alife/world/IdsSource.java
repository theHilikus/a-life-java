package com.github.thehilikus.alife.world;

import com.github.thehilikus.alife.api.AgentScope;
import dagger.Module;
import dagger.Provides;

/**
 * A global assigner of sequential ids
 */
@Module
public class IdsSource {
    private static int nextId;

    @Provides
    @AgentScope
    public static int getNextId() {
        return nextId++;
    }
}
