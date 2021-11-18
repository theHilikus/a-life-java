package com.github.thehilikus.alife.ui;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests for {@link AgentKeyframe}
 */
public class AgentKeyframeTest {
    private static final String ORIENTATION = "orientation";
    private static final double MID_POINT = 0.5;
    private AgentKeyframe testingUnit;

    private static final int EAST = 0;
    private static final int SOUTH = 90;
    private static final int WEST = 180;
    private static final int NORTH = 270;

    @BeforeMethod
    public void setup() {
        testingUnit = new AgentKeyframe(1, 1);
    }

    @Test
    void testAngleInterpolationFromNorth() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, NORTH);
        AgentKeyframe endFrame = new AgentKeyframe(1, 1);
        endFrame.addPropertyToInterpolate(ORIENTATION, EAST);
        AgentKeyframe interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), 315);

        endFrame.addPropertyToInterpolate(ORIENTATION, SOUTH);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(NORTH, SOUTH));

        endFrame.addPropertyToInterpolate(ORIENTATION, WEST);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(NORTH, WEST));
    }

    @Test
    void testAngleInterpolationFromEast() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, EAST);
        AgentKeyframe endFrame = new AgentKeyframe(1, 1);
        endFrame.addPropertyToInterpolate(ORIENTATION, SOUTH);
        AgentKeyframe interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(EAST, SOUTH));

        endFrame.addPropertyToInterpolate(ORIENTATION, WEST);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(EAST, WEST));

        endFrame.addPropertyToInterpolate(ORIENTATION, NORTH);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), 315);
    }

    @Test
    void testAngleInterpolationFromSouth() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, SOUTH);
        AgentKeyframe endFrame = new AgentKeyframe(1, 1);
        endFrame.addPropertyToInterpolate(ORIENTATION, WEST);
        AgentKeyframe interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(SOUTH, WEST));

        endFrame.addPropertyToInterpolate(ORIENTATION, NORTH);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(SOUTH, NORTH));

        endFrame.addPropertyToInterpolate(ORIENTATION, EAST);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(SOUTH, EAST));
    }

    @Test
    void testAngleInterpolationFromWest() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, WEST);
        AgentKeyframe endFrame = new AgentKeyframe(1, 1);
        endFrame.addPropertyToInterpolate(ORIENTATION, NORTH);
        AgentKeyframe interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(WEST, NORTH));

        endFrame.addPropertyToInterpolate(ORIENTATION, EAST);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(WEST, EAST));


        endFrame.addPropertyToInterpolate(ORIENTATION, SOUTH);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(WEST, SOUTH));
    }

    private int midAngle(int first, int second) {
        return (first + second) / 2;
    }
}