package com.github.thehilikus.alife.agents.animals;

import com.github.thehilikus.alife.api.AgentScope;
import com.github.thehilikus.alife.world.IdsProvider;
import com.github.thehilikus.alife.world.WorldComponent;
import dagger.Component;

/**
 *
 */
@AgentScope
@Component(modules = {HerbivoreModule.class, IdsProvider.class}, dependencies = WorldComponent.class)
public interface HerbivoreComponent {
    Herbivore createHerbivore();
}
