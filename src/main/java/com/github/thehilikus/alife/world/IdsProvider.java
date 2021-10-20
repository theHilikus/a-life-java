package com.github.thehilikus.alife.world;

/**
 * A global assigner of sequential ids
 */
public class IdsProvider {
    private static int nextId;

    public static int getNextId() {
        return nextId++;
    }
}
