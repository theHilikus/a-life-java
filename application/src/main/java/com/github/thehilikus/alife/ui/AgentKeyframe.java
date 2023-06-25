package com.github.thehilikus.alife.ui;

import com.github.thehilikus.alife.agent.api.AgentDetails;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An animation frame of a single agent
 */
public class AgentKeyframe implements Comparable<AgentKeyframe> {
    private final Map<String, Object> propertiesToInterpolate = new HashMap<>();
    private final AgentDetails.Immutable agentDetails;
    private final Map<String, Object> fixedProperties;
    private final int agentId;
    private final int zOrder;


    public AgentKeyframe(int agentId, int zOrder, AgentDetails.Immutable agentDetails) {
        this(agentId, zOrder, agentDetails, new HashMap<>());
    }

    private AgentKeyframe(int agentId, int zOrder, AgentDetails.Immutable agentDetails, Map<String, Object> fixedProperties) {
        this.agentId = agentId;
        this.zOrder = zOrder;
        this.agentDetails = agentDetails;
        this.fixedProperties = fixedProperties;
    }

    public void addPropertyToInterpolate(String name, Object value) {
        propertiesToInterpolate.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInterpolatedProperty(String name) {
        return (T) propertiesToInterpolate.get(name);
    }

    public <T> T getAgentDetail(String name) {
        return agentDetails.getAttribute(name);
    }

    public AgentDetails.Immutable getAgentDetails() {
        return agentDetails;
    }

    public AgentKeyframe interpolate(AgentKeyframe endFrame, double percentToKeyFrame) {
        AgentKeyframe result = new AgentKeyframe(zOrder, agentId, agentDetails, fixedProperties);
        for (Map.Entry<String, Object> property : propertiesToInterpolate.entrySet()) {
            String propertyKey = property.getKey();
            if (propertyKey.equals("orientation")) {
                int startAngle = (int) property.getValue();
                int endAngle = endFrame.getInterpolatedProperty(propertyKey);
                result.addPropertyToInterpolate(propertyKey, interpolateAngle(startAngle, endAngle, percentToKeyFrame));
            } else if (property.getValue() instanceof Number startNumber) {
                Number endNumber = endFrame.getInterpolatedProperty(propertyKey);
                result.addPropertyToInterpolate(propertyKey, interpolateNumber(startNumber, endNumber, percentToKeyFrame));
            } else if (property.getValue() instanceof Position.Immutable startPosition) {
                Position.Immutable endPosition = endFrame.getInterpolatedProperty(propertyKey);
                result.addPropertyToInterpolate(propertyKey, interpolatePosition(startPosition, endPosition, percentToKeyFrame));
            } else if (property.getValue() instanceof Color startColor) {
                Color endColor = endFrame.getInterpolatedProperty(propertyKey);
                result.addPropertyToInterpolate(propertyKey, interpolateColor(startColor, endColor, percentToKeyFrame));
            } else {
                throw new UnsupportedOperationException("Don't know how to interpolate type " + property.getValue().getClass());
            }
        }

        return result;
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
            if (endAngle - startAngle > Locomotion.Turn.HALF) {
                //anti is shorter
                result = (int) interpolateNumber(startAngle + Locomotion.Turn.FULL, endAngle, percentToKeyFrame);
            } else {
                // clockwise is shorter
                result = (int) interpolateNumber(startAngle, endAngle, percentToKeyFrame);
            }
        } else { //endAngle < startAngle
            //turned anticlock or wrapped around
            if (startAngle - endAngle > Locomotion.Turn.HALF) {
                //clock is shorter
                result = (int) interpolateNumber(startAngle, endAngle + Locomotion.Turn.FULL, percentToKeyFrame);
            } else {
                result = (int) interpolateNumber(startAngle, endAngle, percentToKeyFrame);
            }

        }

        return result % (Locomotion.Turn.FULL);
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

    @Override
    public int compareTo(AgentKeyframe o) {
        int zOrderDiff = zOrder - o.zOrder;
        if (zOrderDiff == 0) {
            return o.agentId - agentId;
        }
        return zOrderDiff;
    }

    public int getAgentId() {
        return agentId;
    }
}
