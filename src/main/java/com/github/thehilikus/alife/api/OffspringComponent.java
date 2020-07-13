package com.github.thehilikus.alife.api;

import com.github.thehilikus.alife.agents.animals.Herbivore;
import com.github.thehilikus.alife.agents.animals.HerbivoreModule;
import com.github.thehilikus.alife.agents.animals.OffspringModule;
import com.github.thehilikus.alife.agents.controllers.VitalsModule;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.world.IdsProvider;
import com.github.thehilikus.alife.world.World;
import dagger.BindsInstance;
import dagger.Component;

/**
 * Dagger component to build agents that have parents
 */
@AgentScope
@Component(modules = {IdsProvider.class, HerbivoreModule.class, VitalsModule.class, OffspringModule.class})
public interface OffspringComponent {
    Herbivore createOffspring();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder offspringGenome(Genome offspringGenome);

        @BindsInstance
        Builder world(World world);

        OffspringComponent build();
    }
}
