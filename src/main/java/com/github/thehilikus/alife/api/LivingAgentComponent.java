package com.github.thehilikus.alife.api;

import com.github.thehilikus.alife.agents.animals.FirstGenomeModule;
import com.github.thehilikus.alife.agents.animals.Herbivore;
import com.github.thehilikus.alife.agents.animals.HerbivoreModule;
import com.github.thehilikus.alife.agents.controllers.VitalsModule;
import com.github.thehilikus.alife.agents.plants.Plant;
import com.github.thehilikus.alife.agents.plants.PlantModule;
import com.github.thehilikus.alife.world.IdsProvider;
import dagger.Component;

/**
 * Dagger component to build different agents
 */
@AgentScope
@Component(modules = {IdsProvider.class, HerbivoreModule.class, VitalsModule.class, PlantModule.class, FirstGenomeModule.class})
public interface LivingAgentComponent {
    Herbivore createHerbivore();
    Plant createPlant();
}
