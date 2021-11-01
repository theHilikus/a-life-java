package com.github.thehilikus.alife.agents.views;

import com.github.thehilikus.alife.api.Orientation;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static java.lang.Math.PI;
import static org.testng.Assert.*;

/**
 * Tests for {@link Frame}
 */
public class FrameTest {
    private static final String ORIENTATION = "orientation";
    private static final double DELTA = 0.01;
    private static final double MID_POINT = 0.5;
    private Frame testingUnit;

    @BeforeTest
    public void setup() {
        testingUnit = new Frame();
    }

    @Test
    void testAngleInterpolationFromNorth() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, Orientation.NORTH.toRadians());
        Frame endFrame = new Frame();
        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.EAST.toRadians());
        Frame interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals(interpolated.getInterpolatedProperty(ORIENTATION), PI / 4, DELTA);

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.SOUTH.toRadians());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals(interpolated.getInterpolatedProperty(ORIENTATION), PI / 2, DELTA);

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.WEST.toRadians());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals(interpolated.getInterpolatedProperty(ORIENTATION), 7 * PI / 4, DELTA);
    }

    @Test
    void testAngleInterpolationFromEast() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, Orientation.EAST.toRadians());
        Frame endFrame = new Frame();
        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.SOUTH.toRadians());
        Frame interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals(interpolated.getInterpolatedProperty(ORIENTATION), 3 * PI / 4, DELTA);

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.WEST.toRadians());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals(interpolated.getInterpolatedProperty(ORIENTATION), PI, DELTA);

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.NORTH.toRadians());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals(interpolated.getInterpolatedProperty(ORIENTATION), PI / 4, DELTA);
    }

    @Test
    void testAngleInterpolationFromSouth() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, Orientation.SOUTH.toRadians());
        Frame endFrame = new Frame();
        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.WEST.toRadians());
        Frame interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals(interpolated.getInterpolatedProperty(ORIENTATION), 5 * PI / 4, DELTA);

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.NORTH.toRadians());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals(interpolated.getInterpolatedProperty(ORIENTATION), 3 * PI / 2, DELTA);

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.EAST.toRadians());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals(interpolated.getInterpolatedProperty(ORIENTATION), 3 * PI / 4, DELTA);
    }

    @Test
    void testAngleInterpolationFromWest() {
        testingUnit.addPropertyToInterpolate(ORIENTATION, Orientation.WEST.toRadians());
        Frame endFrame = new Frame();
        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.NORTH.toRadians());
        Frame interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals(interpolated.getInterpolatedProperty(ORIENTATION), 7 * PI / 4, DELTA);

        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.EAST.toRadians());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals(interpolated.getInterpolatedProperty(ORIENTATION), 0, DELTA);


        endFrame.addPropertyToInterpolate(ORIENTATION, Orientation.SOUTH.toRadians());
        interpolated = testingUnit.interpolate(endFrame, MID_POINT);
        assertEquals(interpolated.getInterpolatedProperty(ORIENTATION), 5 * PI / 4, DELTA);
    }
}