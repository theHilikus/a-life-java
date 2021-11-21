package com.github.thehilikus.alife.agent.motion;

import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.genetics.DummyGenome;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.vision.api.ScanResult;
import com.github.thehilikus.alife.world.Edge;
import com.github.thehilikus.alife.world.World;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.awt.*;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.testng.Assert.*;

/**
 * Tests {@link Legs}
 */
public class LegsTest {

    private static final double SPEED_FACTOR = 0.5;
    private static final int TOP_SPEED = 100;
    private static final int ORIENTATION = Locomotion.Orientation.SOUTH;
    private static final int ORIGINAL_COORD = 10;
    private static final int WORLD_SIZE = 202;
    private Legs testingUnit;

    @BeforeMethod
    public void setup() {
        Position position = new Position(ORIGINAL_COORD, ORIGINAL_COORD);
        Genome genome = new DummyGenome(Map.of(
                Locomotion.PARAMETER_PREFIX + "topSpeed", TOP_SPEED,
                Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor", 1.0)); //set it to 1 so energy used = distance moved
        testingUnit = new Legs(WORLD_SIZE, WORLD_SIZE, 1, position, ORIENTATION, genome);
    }

    @Test
    void testMoveFreely() {
        double distance = testingUnit.move(SPEED_FACTOR, Collections.emptySortedSet());
        assertEquals(distance, TOP_SPEED * SPEED_FACTOR);
        assertEquals(testingUnit.getOrientation(), ORIENTATION);
        Position.Immutable expectedPosition = new Position(ORIGINAL_COORD, (int) (ORIGINAL_COORD + TOP_SPEED * SPEED_FACTOR)).toImmutable();
        assertEquals(testingUnit.getPosition(), expectedPosition);
    }

    @Test
    void testMoveWithEdgeInSightButNotReached() {
        SortedSet<ScanResult> scanResults = new TreeSet<>();
        int edgeY = ORIGINAL_COORD + TOP_SPEED; //unreachable
        Edge edge = new Edge(new Position(ORIGINAL_COORD, edgeY).toImmutable());
        scanResults.add(new ScanResult(TOP_SPEED * TOP_SPEED, 0, edge));
        Edge edge2 = new Edge(new Position(ORIGINAL_COORD - 1, edgeY).toImmutable());
        scanResults.add(new ScanResult(TOP_SPEED * TOP_SPEED, 1, edge2));

        double distance = testingUnit.move(SPEED_FACTOR, scanResults);
        assertEquals(distance, TOP_SPEED * SPEED_FACTOR);
        assertEquals(testingUnit.getOrientation(), ORIENTATION);
        Position.Immutable expectedPosition = new Position(ORIGINAL_COORD, (int) (ORIGINAL_COORD + TOP_SPEED * SPEED_FACTOR)).toImmutable();
        assertEquals(testingUnit.getPosition(), expectedPosition);
    }

    @Test
    void testMoveWithEdgeInSightAndReached() {
        SortedSet<ScanResult> scanResults = new TreeSet<>();
        Edge edge = new Edge(new Position(ORIGINAL_COORD, WORLD_SIZE + 1).toImmutable());
        scanResults.add(new ScanResult((int) Math.pow(WORLD_SIZE - ORIGINAL_COORD, 2), 0, edge));
        Edge edge2 = new Edge(new Position(ORIGINAL_COORD - 1, WORLD_SIZE + 1).toImmutable());
        scanResults.add(new ScanResult((int) Math.pow(WORLD_SIZE - ORIGINAL_COORD, 2), 1, edge2));

        double distance = testingUnit.move(2, scanResults);

        assertEquals(distance, WORLD_SIZE - 2 - ORIGINAL_COORD);
        assertEquals(testingUnit.getOrientation(), Locomotion.Orientation.NORTH);
        Position.Immutable expectedPosition = new Position(ORIGINAL_COORD, WORLD_SIZE - 2).toImmutable();
        assertEquals(testingUnit.getPosition(), expectedPosition);
    }

    @Test
    void testMoveTowardsTargetInCurrentDirection() {
        Position.Immutable target = new Position(10, 20).toImmutable();

        double distance = testingUnit.moveTowardsTarget(SPEED_FACTOR, target);

        assertEquals(distance, 9);
        Position.Immutable expectedPosition = new Position(ORIGINAL_COORD, ORIGINAL_COORD + 9).toImmutable();
        assertEquals(testingUnit.getPosition(), expectedPosition);

        distance = testingUnit.moveTowardsTarget(SPEED_FACTOR, target);

        assertEquals(distance, 0);
        assertEquals(testingUnit.getOrientation(), ORIENTATION);
    }

    @Test
    void testMoveTowardsTargetInDifferentDirection() {
        final int orientationOffset = 90;
        Position.Immutable target = new Position(0, 10).toImmutable();

        double distance = testingUnit.moveTowardsTarget(SPEED_FACTOR, target);

        assertEquals(distance, 0);

        distance = testingUnit.moveTowardsTarget(SPEED_FACTOR, target);

        assertEquals(distance, 9);
        assertEquals(testingUnit.getOrientation(), ORIENTATION + orientationOffset);
        Position.Immutable expectedPosition = new Position(ORIGINAL_COORD - 9, ORIGINAL_COORD).toImmutable();
        assertEquals(testingUnit.getPosition(), expectedPosition);
    }

    @Test
    void testMoveCloseToEdgeAndImpactIt() {
        int orientation = 4;

        Legs legs = moveTowardsWall(150, 200, orientation);

        assertEquals(legs.getPosition().getY(), 200);
        assertEquals(legs.getOrientation(), Locomotion.Turn.FULL - 86);
    }

    @Test
    void testMoveCloseToEdgeNoImpactingIt() {
        int orientation = 0;
        int originalX = 50;
        Legs legs = moveTowardsWall(originalX, 200, orientation);

        assertEquals(legs.getPosition().getX(), TOP_SPEED + originalX);
        assertEquals(legs.getPosition().getY(), 200);
        assertEquals(legs.getOrientation(), orientation);
    }

    @Test
    void testBounceOffWestWallFacingSouthWest() {
        int orientation = 135;

        Legs legs = moveTowardsWall(5, 100, orientation);

        assertEquals(legs.getPosition().getX(), 1, "New position not next to edge");
        assertEquals(legs.getOrientation(), orientation + Locomotion.Turn.LEFT);
    }

    @Test
    void testBounceOffWestWallFacingNorthWest() {
        int orientation = 230;

        Legs legs = moveTowardsWall(5, 100, orientation);

        assertEquals(legs.getPosition().getX(), 1, "New position not next to edge");
        assertEquals(legs.getOrientation(), orientation + Locomotion.Turn.RIGHT);
    }

    @Test
    void testBounceOffNorthWallFacingNorthWest() {
        int orientation = 230;

        Legs legs = moveTowardsWall(100, 5, orientation);

        assertEquals(legs.getPosition().getY(), 1, "New position not next to edge");
        assertEquals(legs.getOrientation(), orientation + Locomotion.Turn.LEFT);
    }

    @Test
    void testBounceOffNorthWallFacingNorthEast() {
        int orientation = 350;

        Legs legs = moveTowardsWall(100, 5, orientation);

        assertEquals(legs.getPosition().getY(), 1, "New position not next to edge");
        assertEquals(legs.getOrientation(), (orientation + Locomotion.Turn.RIGHT) % Locomotion.Turn.FULL);
    }

    @Test
    void testBounceOffEastWallFacingNorthEast() {
        int orientation = 280;

        Legs legs = moveTowardsWall(195, 100, orientation);

        assertEquals(legs.getPosition().getX(), 200, "New position not next to edge");
        assertEquals(legs.getOrientation(), orientation + Locomotion.Turn.LEFT);
    }

    @Test
    void testBounceOffEastWallFacingSouthEast() {
        int orientation = 80;

        Legs legs = moveTowardsWall(195, 100, orientation);

        assertEquals(legs.getPosition().getX(), 200, "New position not next to edge");
        assertEquals(legs.getOrientation(), orientation + Locomotion.Turn.RIGHT);
    }

    @Test
    void testBounceOffSouthWallFacingSouthEast() {
        int orientation = 10;

        Legs legs = moveTowardsWall(100, 195, orientation);

        assertEquals(legs.getPosition().getY(), 200, "New position not next to edge");
        assertEquals(legs.getOrientation(), Locomotion.Turn.FULL + orientation + Locomotion.Turn.LEFT);
    }

    @Test
    void testBounceOffSouthWallFacingSouthWest() {
        int orientation = 170;

        Legs legs = moveTowardsWall(100, 195, orientation);

        assertEquals(legs.getPosition().getY(), 200, "New position not next to edge");
        assertEquals(legs.getOrientation(), orientation + Locomotion.Turn.RIGHT);
    }

    private Legs moveTowardsWall(int x, int y, int orientation) {
        int agentId = 1;
        Legs legs = createDummyLegs(agentId, x, y, orientation);
        World dummyWorld = new World(WORLD_SIZE, WORLD_SIZE);
        dummyWorld.addAgent(new DummyAgent(legs));
        Shape viewRectangle = new Rectangle(-100, -100, 200, 200);
        SortedSet<ScanResult> edges = dummyWorld.getAgentsInAreaRelativeTo(agentId, viewRectangle, Edge.class::isInstance);

        legs.move(1, edges);

        return legs;
    }

    private Legs createDummyLegs(int id, int x, int y, int orientation) {
        Genome genome = new DummyGenome(Map.of(
                Locomotion.PARAMETER_PREFIX + "topSpeed", TOP_SPEED,
                Locomotion.PARAMETER_PREFIX + "energyExpenditureFactor", 1.0)); //set it to 1 so energy used = distance moved

        return new Legs(WORLD_SIZE, WORLD_SIZE, id, new Position(x, y), orientation, genome);
    }
}