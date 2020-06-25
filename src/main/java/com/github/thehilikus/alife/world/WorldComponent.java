package com.github.thehilikus.alife.world;

import dagger.Component;

import javax.inject.Singleton;

/**
 * World creator
 */
@Singleton
@Component(modules = World.class)
public interface WorldComponent {
    World createWorld();
}
