package com.github.thehilikus.alife.agents.views;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.controllers.HungerTracker;
import com.github.thehilikus.alife.api.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Representations for {@link com.github.thehilikus.alife.agents.animals.Herbivore}
 */
public class HerbivoreView implements Agent.View {

    private static final double OLD_AGE_PROPORTION = 0.20;
    private static final Map<String, Ansi.FColor> consoleMoodColours = Map.of(
            "Eating", Ansi.FColor.GREEN,
            "Existing", Ansi.FColor.WHITE,
            "Hunting", Ansi.FColor.BLUE,
            "InHeat", Ansi.FColor.WHITE,
            "InHeatChasing", Ansi.FColor.MAGENTA,
            "Mating", Ansi.FColor.RED,
            "Scouting", Ansi.FColor.CYAN,
            "Sleeping", Ansi.FColor.YELLOW
    );

    /**
     * Mapping of moods to colours
     */
    public static final Map<String, Color> graphicalMoodColours = Map.of(
            "Eating", Color.GREEN,
            "Existing", Color.GRAY,
            "Hunting", Color.BLUE,
            "InHeat", Color.PINK,
            "InHeatChasing", Color.MAGENTA,
            "Mating", Color.RED,
            "Scouting", Color.CYAN,
            "Sleeping", Color.ORANGE
    );

    private final Map<Agent, Frame> lastKeyframes = new HashMap<>();

    @Override
    public void drawInConsole(StringBuilder builder, Agent agent) {
        Ansi.Attribute agentTypeStyle = Ansi.Attribute.BOLD;

        Map<String, Object> details = agent.getDetails();
        String moodName = details.get(Mood.PARAMETER_PREFIX + "current").toString();
        Ansi.FColor moodColour = consoleMoodColours.get(moodName);
        Objects.requireNonNull(moodColour, "Console mood colour was empty for " + moodName);
        Ansi.BColor background = Ansi.BColor.NONE;
        String formatCode = Ansi.generateCode(agentTypeStyle, moodColour, background);

        int id = agent.getId();
        String idString = Integer.toString(id);
        if (id < 10) {
            idString = ' ' + idString;
        }
        builder.append(Ansi.formatMessage(idString, formatCode));
    }

    @Override
    public Shape drawKeyframe(Graphics2D g2d, Agent agent, boolean selected) {
        Map<String, Object> details = agent.getDetails();

        Position.Immutable position = (Position.Immutable) details.get("position");
        if (selected) {
            drawVision(g2d, details, agent.getPosition());
            Position.Immutable targetPosition = (Position.Immutable) details.get(Mood.PARAMETER_PREFIX + "targetPosition");
            if (targetPosition != null) {
                drawPathToTarget(g2d, position, targetPosition);
            }
        }

        Frame keyframe = new Frame();
        keyframe.addFixedProperty("size", details.get("size"));
        keyframe.addPropertyToInterpolate("position", details.get("position"));
        keyframe.addPropertyToInterpolate("orientation", details.get(Locomotion.PARAMETER_PREFIX + "orientation"));
        Color agentColor = computeAgentColor(details);
        g2d.setColor(agentColor);
        keyframe.addPropertyToInterpolate("color", agentColor);

        if ((int) details.get(VitalSign.PARAMETER_PREFIX + "age") >= (int) details.get(Agent.Evolvable.PARAMETER_PREFIX + "teenAge")) {
            Stroke stroke = new BasicStroke(2);
            g2d.setStroke(stroke);
            keyframe.addFixedProperty("stroke", stroke);
        }

        Shape agentShape = createHerbivoreShape(keyframe);
        g2d.fill(agentShape);

        Color borderColor = computeBorderColor(details);
        g2d.setColor(borderColor);
        keyframe.addFixedProperty("borderColor", borderColor);
        g2d.draw(agentShape);

        lastKeyframes.put(agent, keyframe);
        return agentShape;
    }

    @Override
    public void drawTweenFrame(Graphics2D g2d, Agent agent, double percentToKeyFrame) {
        Frame previousKeyframe = lastKeyframes.get(agent);
        Frame newKeyframe = new Frame();
        newKeyframe.addPropertyToInterpolate("position", agent.getDetails().get("position"));
        newKeyframe.addPropertyToInterpolate("orientation", agent.getDetails().get(Locomotion.PARAMETER_PREFIX + "orientation"));
        Color newAgentColor = computeAgentColor(agent.getDetails());
        newKeyframe.addPropertyToInterpolate("color", newAgentColor); //TODO: check color interpolation

        if (previousKeyframe == null) {
            //new agent in the world. just take its new keyframe as previous keyframe
            newKeyframe.addFixedProperty("size", agent.getDetails().get("size"));
            previousKeyframe = newKeyframe;
        }
        Frame tweenFrame = previousKeyframe.interpolate(newKeyframe, percentToKeyFrame);

        Color agentColor = previousKeyframe.getInterpolatedProperty("color");
        g2d.setColor(agentColor);
        Stroke stroke = previousKeyframe.getFixedProperty("stroke");
        if (stroke != null) {
            g2d.setStroke(stroke);
        }
        Shape agentShape = createHerbivoreShape(tweenFrame);
        g2d.fill(agentShape);

        Color borderColor = previousKeyframe.getFixedProperty("borderColor");
        g2d.setColor(borderColor);

        g2d.draw(agentShape);
    }

    private void drawVision(Graphics2D g2d, Map<String, Object> details, Position.Immutable position) {
        int distance = (int) details.get(Vision.PARAMETER_PREFIX + "distance");
        int x = position.getX() - distance / 2;
        int y = position.getY() - distance / 2;
        Shape vision = new Rectangle(x, y, distance, distance);

        g2d.setColor(Color.BLUE);
        g2d.draw(vision);
    }

    private void drawPathToTarget(Graphics2D g2d, Position.Immutable position, Position.Immutable targetPosition) {
        g2d.setColor(graphicalMoodColours.get("Hunting"));
        g2d.drawLine(position.getX(), position.getY(), targetPosition.getX(), targetPosition.getY());
    }

    @SuppressWarnings("MagicNumber")
    private Shape createHerbivoreShape(Frame frame) {
        int size = frame.getFixedProperty("size");

        Path2D triangle = new Path2D.Double();
        triangle.moveTo(size / -2.4, -size / 2.0);
        triangle.lineTo(size / -2.4, size / 2.0);
        triangle.lineTo(size / 1.2, 0 );
        triangle.closePath();

        AffineTransform transform = new AffineTransform();

        Position.Immutable position = frame.getInterpolatedProperty("position");
        transform.translate(position.getX(), position.getY());

        int orientation = frame.getInterpolatedProperty("orientation");
        double orientationInRadians = Math.toRadians(orientation);
        transform.rotate(orientationInRadians);

        triangle.transform(transform);

        return triangle;
    }

    private Color computeAgentColor(Map<String, Object> details) {
        String moodName = details.get(Mood.PARAMETER_PREFIX + "current").toString();
        Color moodColor = graphicalMoodColours.get(moodName);
        Objects.requireNonNull(moodColor, "Mood color was empty for " + moodName);
        float[] rgbColorComponents = moodColor.getRGBColorComponents(null);
        float vitality = calculateVitality(details);

        return new Color(rgbColorComponents[0], rgbColorComponents[1], rgbColorComponents[2], vitality);
    }

    private float calculateVitality(Map<String, Object> details) {
        float energy = (int) details.get(VitalSign.PARAMETER_PREFIX + "energy") / (float) EnergyTracker.MAX_ENERGY;
        float hunger = (int) details.get(VitalSign.PARAMETER_PREFIX + "hunger") / (float) HungerTracker.FULL_THRESHOLD;

        return Math.min(energy, hunger);
    }

    private Color computeBorderColor(Map<String, Object> details) {
        int lifeExpectancy = (int) details.get(VitalSign.PARAMETER_PREFIX + "lifeExpectancy");
        double lifeLeftProportion = (lifeExpectancy - (int) details.get(VitalSign.PARAMETER_PREFIX + "age")) / (double) lifeExpectancy;
        if (lifeLeftProportion < OLD_AGE_PROPORTION) {
            return Color.LIGHT_GRAY;
        } else {
            return Color.BLACK;
        }
    }
}
