package com.github.thehilikus.alife.api;

import com.github.thehilikus.alife.agents.animals.Herbivore;
import com.github.thehilikus.alife.agents.animals.HerbivoreModule;
import com.github.thehilikus.alife.agents.plants.Plant;
import com.github.thehilikus.alife.agents.plants.PlantModule;
import com.github.thehilikus.alife.world.IdsProvider;
import com.github.thehilikus.alife.world.WorldComponent;
import dagger.Component;

/**
 *
 */
@AgentScope
@Component(modules = {IdsProvider.class, HerbivoreModule.class, PlantModule.class}, dependencies = WorldComponent.class)
public interface LivingAgentComponent {
    Herbivore createHerbivore();
    Plant createPlant();
}
