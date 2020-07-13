package com.github.thehilikus.alife.agents.animals;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.AgentScope;
import com.github.thehilikus.alife.api.Position;
import com.github.thehilikus.alife.world.World;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for the original herbivores in the world. The ones born without parents
 */
@Module
public class FirstGenomeModule {
    @Provides
    @AgentScope
    Genome provideGenome(int agentId) {
        return new Herbivore.HerbivoreGenome(agentId);
    }

    @Provides
    @AgentScope
    Position provideEmptyPosition(World world) {
        return world.getEmptyPosition();
    }
}
