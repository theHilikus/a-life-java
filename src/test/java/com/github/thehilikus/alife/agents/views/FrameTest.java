package com.github.thehilikus.alife.agents.views;

import com.github.thehilikus.alife.api.Orientation;
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
        testingUnit.addPropertyToInterpolate(ORIENTATION, Orientation.NORTH.toDegrees());
        Frame endFrame = new Frame();
        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.EAST.toDegrees());
        Frame interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), 315);

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.SOUTH.toDegrees());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(Orientation.NORTH, Orientation.SOUTH));

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.WEST.toDegrees());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(Orientation.NORTH, Orientation.WEST));
    }

    @Test
    void testAngleInterpolationFromEast() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, Orientation.EAST.toDegrees());
        Frame endFrame = new Frame();
        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.SOUTH.toDegrees());
        Frame interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(Orientation.EAST, Orientation.SOUTH));

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.WEST.toDegrees());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(Orientation.EAST, Orientation.WEST));

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.NORTH.toDegrees());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), 315);
    }

    @Test
    void testAngleInterpolationFromSouth() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, Orientation.SOUTH.toDegrees());
        Frame endFrame = new Frame();
        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.WEST.toDegrees());
        Frame interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(Orientation.SOUTH, Orientation.WEST));

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.NORTH.toDegrees());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(Orientation.SOUTH, Orientation.NORTH));

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.EAST.toDegrees());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(Orientation.SOUTH, Orientation.EAST));
    }

    @Test
    void testAngleInterpolationFromWest() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, Orientation.WEST.toDegrees());
        Frame endFrame = new Frame();
        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.NORTH.toDegrees());
        Frame interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(Orientation.WEST, Orientation.NORTH));

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.EAST.toDegrees());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(Orientation.WEST, Orientation.EAST));


        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.SOUTH.toDegrees());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals((int) interpolated.getInterpolatedProperty(ORIENTATION), midAngle(Orientation.WEST, Orientation.SOUTH));
    }

    private int midAngle(Orientation first, Orientation second) {
        return (first.toDegrees() + second.toDegrees()) / 2;
    }
}