package com.github.thehilikus.alife.agent.vision.api;

/**
 * A geometric shape
 */
public interface Shape {
    /**
     * Tests if the specified coordinates are inside the boundary of the
     * {@code Shape}, as described by the
     * <a href="{@docRoot}/java.desktop/java/awt/Shape.html#def_insideness">
     * definition of insideness</a>.
     *
     * @param x the specified X coordinate to be tested
     * @param y the specified Y coordinate to be tested
     * @return {@code true} if the specified coordinates are inside
     * the {@code Shape} boundary; {@code false}
     * otherwise.
     */
    boolean contains(double x, double y);
}
