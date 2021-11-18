package com.github.thehilikus.alife.agent.api;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests {@link Position}
 */
public class PositionTest {
    private static final int X = 100;
    private static final int Y = 100;
    private static final int SPEED = 10;
    private Position testingUnit;

    @BeforeMethod
    public void setup() {
        testingUnit = new Position(X, Y);
    }

    @Test
    void testMoveNorth() {
        testingUnit.move(OrientationHelper.NORTH, SPEED);
        assertEquals(X, testingUnit.getX());
        assertEquals(Y - SPEED, testingUnit.getY());
    }

    @Test
    void testMoveEast() {
        testingUnit.move(OrientationHelper.EAST, SPEED);
        assertEquals(X + SPEED, testingUnit.getX());
        assertEquals(Y, testingUnit.getY());
    }

    @Test
    void testMoveSouth() {
        testingUnit.move(OrientationHelper.SOUTH, SPEED);
        assertEquals(X, testingUnit.getX());
        assertEquals(Y + SPEED, testingUnit.getY());
    }

    @Test
    void testMoveWest() {
        testingUnit.move(OrientationHelper.WEST, SPEED);
        assertEquals(X - SPEED, testingUnit.getX());
        assertEquals(Y, testingUnit.getY());
    }

    @Test
    void testMoveSouthWest() {
        int southEast = (OrientationHelper.SOUTH + OrientationHelper.WEST) / 2;
        testingUnit.move(southEast, SPEED);
        int delta = 7; // sin(southEast) * SPEED
        assertEquals(X - delta, testingUnit.getX());
        assertEquals(Y + delta, testingUnit.getY());
    }

    @Test
    void testNextTo() {
        assertTrue(testingUnit.toImmutable().isNextTo(new Position(X + 1, Y).toImmutable()));
        assertTrue(testingUnit.toImmutable().isNextTo(new Position(X, Y + 1).toImmutable()));
        assertTrue(testingUnit.toImmutable().isNextTo(new Position(X + 1, Y + 1).toImmutable()));

        assertTrue(testingUnit.toImmutable().isNextTo(new Position(X - 1, Y).toImmutable()));
        assertTrue(testingUnit.toImmutable().isNextTo(new Position(X, Y - 1).toImmutable()));
        assertTrue(testingUnit.toImmutable().isNextTo(new Position(X - 1, Y - 1).toImmutable()));
    }

    @Test
    void testNotNextTo() {
        assertFalse(testingUnit.toImmutable().isNextTo(new Position(X + 2, Y).toImmutable()));
        assertFalse(testingUnit.toImmutable().isNextTo(new Position(X, Y + 2).toImmutable()));
        assertFalse(testingUnit.toImmutable().isNextTo(new Position(X + 2, Y + 2).toImmutable()));

        assertFalse(testingUnit.toImmutable().isNextTo(new Position(X - 2, Y).toImmutable()));
        assertFalse(testingUnit.toImmutable().isNextTo(new Position(X, Y - 2).toImmutable()));
        assertFalse(testingUnit.toImmutable().isNextTo(new Position(X - 2, Y - 2).toImmutable()));
    }
}