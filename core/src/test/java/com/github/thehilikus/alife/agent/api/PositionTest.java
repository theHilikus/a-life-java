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
        testingUnit.move(0, -SPEED);
        assertEquals(testingUnit.getX(), X);
        assertEquals(testingUnit.getY(), Y - SPEED);
    }

    @Test
    void testMoveEast() {
        testingUnit.move(SPEED, 0);
        assertEquals(testingUnit.getX(), X + SPEED);
        assertEquals(testingUnit.getY(), Y);
    }

    @Test
    void testMoveSouth() {
        testingUnit.move(0, SPEED);
        assertEquals(testingUnit.getX(), X);
        assertEquals(testingUnit.getY(), Y + SPEED);
    }

    @Test
    void testMoveWest() {
        testingUnit.move(-SPEED, 0);
        assertEquals(testingUnit.getX(), X - SPEED);
        assertEquals(testingUnit.getY(), Y);
    }

    @Test
    void testMoveSouthWest() {
        int delta = 7;
        testingUnit.move(-delta, delta);
        assertEquals(testingUnit.getX(), X - delta);
        assertEquals(testingUnit.getY(), Y + delta);
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