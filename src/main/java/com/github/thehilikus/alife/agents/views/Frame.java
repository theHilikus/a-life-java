package com.github.thehilikus.alife.agents.views;

import com.github.thehilikus.alife.api.Orientation;
import com.github.thehilikus.alife.api.Position;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An animation frame
 */
public class Frame {
    private final Map<String, Object> propertiesToInterpolate = new HashMap<>();
    private final Map<String, Object> fixedProperties;

    public Frame() {
        this(new HashMap<>());
    }

    private Frame(Map<String, Object> fixedProperties) {
        this.fixedProperties = fixedProperties;
    }

    public void addPropertyToInterpolate(String name, Object value) {
        propertiesToInterpolate.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInterpolatedProperty(String name) {
        return (T) propertiesToInterpolate.get(name);
    }

    public void addFixedProperty(String name, Object value) {
        fixedProperties.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getFixedProperty(String name) {
        return (T) fixedProperties.get(name);
    }

    public Frame interpolate(Frame endFrame, double percentToKeyFrame) {
        Frame result = new Frame(fixedProperties);
        for (Map.Entry<String, Object> property : propertiesToInterpolate.entrySet()) {
            String propertyKey = property.getKey();
            if (propertyKey.equals("orientation")) {
                int startAngle = (int) property.getValue();
                int endAngle = endFrame.getInterpolatedProperty(propertyKey);
                result.addPropertyToInterpolate(propertyKey, interpolateAngle(startAngle, endAngle, percentToKeyFrame));
            } else if (property.getValue() instanceof Number) {
                Number startNumber = (Number) property.getValue();
                Number endNumber = endFrame.getInterpolatedProperty(propertyKey);
                result.addPropertyToInterpolate(propertyKey, interpolateNumber(startNumber, endNumber, percentToKeyFrame));
            } else if (property.getValue() instanceof Position.Immutable) {
                Position.Immutable startPosition = (Position.Immutable) property.getValue();
                Position.Immutable endPosition = endFrame.getInterpolatedProperty(propertyKey);
                result.addPropertyToInterpolate(propertyKey, interpolatePosition(startPosition, endPosition, percentToKeyFrame));
            } else if (property.getValue() instanceof Color) {
                Color startColor = (Color) property.getValue();
                Color endColor = endFrame.getInterpolatedProperty(propertyKey);
                result.addPropertyToInterpolate(propertyKey, interpolateColor(startColor, endColor, percentToKeyFrame));
            } else {
                throw new UnsupportedOperationException("Don't know how to interpolate type " + property.getValue().getClass());
            }
        }

        return result;
    }

    private Number interpolateNumber(Number startValue, Number endValue, double percentToKeyFrame) {
        Objects.requireNonNull(startValue);
        Objects.requireNonNull(endValue);
        if (startValue instanceof Integer) {
            return (int) (startValue.intValue() + (endValue.intValue() - startValue.intValue()) * percentToKeyFrame);
        }
        if (startValue instanceof Double) {
            return startValue.doubleValue() + (endValue.doubleValue() - startValue.doubleValue()) * percentToKeyFrame;
        }

        throw new UnsupportedOperationException("Don't know how to interpolate Number " + startValue.getClass());
    }

    private Position.Immutable interpolatePosition(Position.Immutable startPosition, Position.Immutable endPosition, double percentToKeyFrame) {
        Objects.requireNonNull(startPosition);
        Objects.requireNonNull(endPosition);
        int interX = (int) interpolateNumber(startPosition.getX(), endPosition.getX(), percentToKeyFrame);
        int interY = (int) interpolateNumber(startPosition.getY(), endPosition.getY(), percentToKeyFrame);

        return new Position(interX, interY).toImmutable();
    }

    private int interpolateAngle(int startAngle, int endAngle, double percentToKeyFrame) {
        int result;
        if (endAngle > startAngle) {
            //turned clockwise. see if anti clock is shorter
            if (endAngle - startAngle > Orientation.HALF_TURN) {
                //anti is shorter
                result = (int) interpolateNumber(startAngle + Orientation.FULL_TURN, endAngle, percentToKeyFrame);
            } else {
                // clockwise is shorter
                result = (int) interpolateNumber(startAngle, endAngle, percentToKeyFrame);
            }
        } else { //endAngle < startAngle
            //turned anticlock or wrapped around
            if (startAngle - endAngle > Orientation.HALF_TURN) {
                //clock is shorter
                result = (int) interpolateNumber(startAngle, endAngle + Orientation.FULL_TURN, percentToKeyFrame);
            } else {
                result = (int) interpolateNumber(startAngle, endAngle, percentToKeyFrame);
            }

        }

        return result % (Orientation.FULL_TURN);
    }

    private Color interpolateColor(Color startColor, Color endColor, double percentToKeyFrame) {
        Objects.requireNonNull(startColor);
        Objects.requireNonNull(endColor);

        int red = (int) interpolateNumber(startColor.getRed(), endColor.getRed(), percentToKeyFrame);
        int green = (int) interpolateNumber(startColor.getGreen(), endColor.getGreen(), percentToKeyFrame);
        int blue = (int) interpolateNumber(startColor.getBlue(), endColor.getBlue(), percentToKeyFrame);
        int alpha = (int) interpolateNumber(startColor.getAlpha(), endColor.getAlpha(), percentToKeyFrame);

        return new Color(red, green, blue, alpha);
    }
}
