package com.github.thehilikus.alife.agents.views;

import com.github.thehilikus.alife.api.OrientationHelper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tests for {@link Frame}
 */
public class FrameTest {
    private static final String ORIENTATION = "orientation";
    private static final double MID_POINT = 0.5;
    private Frame testingUnit;

    @BeforeMethod
    public void setup() {
        testingUnit = new Frame();
    }

    @Test
    void testAngleInterpolationFromNorth() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, OrientationHelper.NORTH);
        Frame endFrame = new Frame();
        endFrame.addPropertyToInterpolate(ORIENTATION, OrientationHelper.EAST);
        Frame interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), 315);

        endFrame.addPropertyToInterpolate(ORIENTATION, OrientationHelper.SOUTH);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(OrientationHelper.NORTH, OrientationHelper.SOUTH));

        endFrame.addPropertyToInterpolate(ORIENTATION, OrientationHelper.WEST);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(OrientationHelper.NORTH, OrientationHelper.WEST));
    }

    @Test
    void testAngleInterpolationFromEast() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, OrientationHelper.EAST);
        Frame endFrame = new Frame();
        endFrame.addPropertyToInterpolate(ORIENTATION, OrientationHelper.SOUTH);
        Frame interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(OrientationHelper.EAST, OrientationHelper.SOUTH));

        endFrame.addPropertyToInterpolate(ORIENTATION, OrientationHelper.WEST);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(OrientationHelper.EAST, OrientationHelper.WEST));

        endFrame.addPropertyToInterpolate(ORIENTATION, OrientationHelper.NORTH);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), 315);
    }

    @Test
    void testAngleInterpolationFromSouth() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, OrientationHelper.SOUTH);
        Frame endFrame = new Frame();
        endFrame.addPropertyToInterpolate(ORIENTATION, OrientationHelper.WEST);
        Frame interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(OrientationHelper.SOUTH, OrientationHelper.WEST));

        endFrame.addPropertyToInterpolate(ORIENTATION, OrientationHelper.NORTH);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(OrientationHelper.SOUTH, OrientationHelper.NORTH));

        endFrame.addPropertyToInterpolate(ORIENTATION, OrientationHelper.EAST);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(OrientationHelper.SOUTH, OrientationHelper.EAST));
    }

    @Test
    void testAngleInterpolationFromWest() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, OrientationHelper.WEST);
        Frame endFrame = new Frame();
        endFrame.addPropertyToInterpolate(ORIENTATION, OrientationHelper.NORTH);
        Frame interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(OrientationHelper.WEST, OrientationHelper.NORTH));

        endFrame.addPropertyToInterpolate(ORIENTATION, OrientationHelper.EAST);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(OrientationHelper.WEST, OrientationHelper.EAST));


        endFrame.addPropertyToInterpolate(ORIENTATION, OrientationHelper.SOUTH);
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(OrientationHelper.WEST, OrientationHelper.SOUTH));
    }

    private int midAngle(int first, int second) {
        return (first + second) / 2;
    }
}