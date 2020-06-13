package com.github.thehilikus.alife.world;

/**
 * A global assigner of sequential ids
 */
public class IdsSource {
    private static int nextId;

    public static int getNextId() {
        return nextId++;
    }
}
