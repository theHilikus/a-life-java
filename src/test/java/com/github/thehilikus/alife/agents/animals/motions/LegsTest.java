package com.github.thehilikus.alife.agents.animals.motions;

import com.github.thehilikus.alife.agents.genetics.DummyGenome;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.Edge;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

/**
 * Tests {@link Legs}
 */
public class LegsTest {

    private static final double SPEED_FACTOR = 0.5;
    private static final int TOP_SPEED = 20;
    private static final int ORIENTATION = OrientationHelper.SOUTH;
    private static final int ORIGINAL_COORD = 10;
    private Legs testingUnit;

    @BeforeMethod
    public void setup() {
        Position position = new Position(ORIGINAL_COORD, ORIGINAL_COORD);
        Genome genome = new DummyGenome(Map.of(
                Locomotion.PARAMETER_PREFIX + "topSpeed", TOP_SPEED,
                Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor", 0.10));
        testingUnit = new Legs(1, position, ORIENTATION, genome);
    }

    @Test
    void testMoveFreely() {
        int distance = testingUnit.move(SPEED_FACTOR, Collections.emptySortedSet());
        assertEquals(distance, TOP_SPEED * SPEED_FACTOR);
        assertEquals(testingUnit.getOrientation(), ORIENTATION);
        Position.Immutable expectedPosition = new Position(ORIGINAL_COORD, (int) (ORIGINAL_COORD + TOP_SPEED * SPEED_FACTOR)).toImmutable();
        assertEquals(testingUnit.getPosition(), expectedPosition);
    }

    @Test
    void testMoveWithEdgeInSightButNotReached() {
        SortedSet<ScanResult> scanResults = new TreeSet<>();
        int edgeY = ORIGINAL_COORD * ORIGINAL_COORD; //unreachable
        Edge edge = new Edge(new Position(ORIGINAL_COORD, edgeY).toImmutable());
        scanResults.add(new ScanResult(TOP_SPEED * TOP_SPEED, 0, edge));
        Edge edge2 = new Edge(new Position(ORIGINAL_COORD - 1, edgeY).toImmutable());
        scanResults.add(new ScanResult(TOP_SPEED * TOP_SPEED, 1, edge2));

        int distance = testingUnit.move(SPEED_FACTOR, scanResults);
        assertEquals(distance, TOP_SPEED * SPEED_FACTOR);
        assertEquals(testingUnit.getOrientation(), ORIENTATION);
        Position.Immutable expectedPosition = new Position(ORIGINAL_COORD, (int) (ORIGINAL_COORD + TOP_SPEED * SPEED_FACTOR)).toImmutable();
        assertEquals(testingUnit.getPosition(), expectedPosition);
    }

    @Test
    void testMoveWithEdgeInSightAndReached() {
        SortedSet<ScanResult> scanResults = new TreeSet<>();
        int edgeY = ORIGINAL_COORD + 5; //reachable
        Edge edge = new Edge(new Position(ORIGINAL_COORD, edgeY).toImmutable());
        scanResults.add(new ScanResult(25, 0, edge));
        Edge edge2 = new Edge(new Position(ORIGINAL_COORD - 1, edgeY).toImmutable());
        scanResults.add(new ScanResult(25, 1, edge2));

        int distance = testingUnit.move(SPEED_FACTOR, scanResults);
        assertEquals(distance, edgeY - ORIGINAL_COORD - 1);
        assertEquals(testingUnit.getOrientation(), OrientationHelper.NORTH);
        Position.Immutable expectedPosition = new Position(ORIGINAL_COORD, edgeY - 1).toImmutable();
        assertEquals(testingUnit.getPosition(), expectedPosition);
    }

    @Test
    void testMoveTowardsTargetInCurrentDirection() {
        int distance = testingUnit.moveTowardsTarget(SPEED_FACTOR, 8, 0);
        assertEquals(distance, 7);
        distance = testingUnit.moveTowardsTarget(SPEED_FACTOR, 1, 0);
        assertEquals(distance, 0);
        assertEquals(testingUnit.getOrientation(), ORIENTATION);
        Position.Immutable expectedPosition = new Position(ORIGINAL_COORD, ORIGINAL_COORD + 7).toImmutable();
        assertEquals(testingUnit.getPosition(), expectedPosition);
    }

    @Test
    void testMoveTowardsTargetInDifferentDirection() {
        final int orientationOffset = 90;
        int distance = testingUnit.moveTowardsTarget(SPEED_FACTOR, 8, orientationOffset);
        assertEquals(distance, 0);
        distance = testingUnit.moveTowardsTarget(SPEED_FACTOR, 8, 0);
        assertEquals(distance, 7);
        assertEquals(testingUnit.getOrientation(), ORIENTATION + orientationOffset);
        Position.Immutable expectedPosition = new Position(ORIGINAL_COORD - 7, ORIGINAL_COORD).toImmutable();
        assertEquals(testingUnit.getPosition(), expectedPosition);
    }
}