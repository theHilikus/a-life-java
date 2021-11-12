package com.github.thehilikus.alife.agents.views;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.controllers.HungerTracker;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.ui.AgentKeyframe;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
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
    private static final int Z_ORDER = 10;

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
    public AgentKeyframe createAgentFrame(Agent agent) {
        Map<String, Object> details = agent.getDetails();
        AgentKeyframe result = new AgentKeyframe(agent.getId(), Z_ORDER, details);
        result.addPropertyToInterpolate("position", details.get("position"));
        result.addPropertyToInterpolate("orientation", details.get(Locomotion.PARAMETER_PREFIX + "orientation"));
        Color agentColor = computeAgentColor(details);
        result.addPropertyToInterpolate("color", agentColor);

        return result;
    }

    @Override
    public Shape drawKeyframe(Graphics2D g2d, AgentKeyframe newKeyframe, boolean selected) {

        Position.Immutable position = newKeyframe.getInterpolatedProperty("position");
        if (selected) {
            int distance = newKeyframe.getAgentDetail(Vision.PARAMETER_PREFIX + "distance");
            drawVision(g2d, distance, position);
            Position.Immutable targetPosition = newKeyframe.getAgentDetail(Mood.PARAMETER_PREFIX + "targetPosition");
            if (targetPosition != null) {
                drawPathToTarget(g2d, position, targetPosition);
            }
        }

        Color agentColor = newKeyframe.getInterpolatedProperty("color");
        g2d.setColor(agentColor);

        Stroke stroke = new BasicStroke();
        if ((int) newKeyframe.getAgentDetail(VitalSign.PARAMETER_PREFIX + "age") >= (int) newKeyframe.getAgentDetail(Agent.Evolvable.PARAMETER_PREFIX + "teenAge")) {
            stroke = new BasicStroke(2);
        }
        g2d.setStroke(stroke);

        Shape agentShape = createHerbivoreShape(newKeyframe);
        g2d.fill(agentShape);

        Color borderColor = computeBorderColor(newKeyframe.getAgentDetails());
        g2d.setColor(borderColor);
        g2d.draw(agentShape);

        return agentShape;
    }

    @Override
    public void drawTweenFrame(Graphics2D g2d, AgentKeyframe lastKeyframe, AgentKeyframe newKeyframe, double percentToKeyframe) {
        AgentKeyframe tweenFrame = lastKeyframe.interpolate(newKeyframe, percentToKeyframe);

        Color agentColor = tweenFrame.getInterpolatedProperty("color");
        g2d.setColor(agentColor);
        Stroke stroke = new BasicStroke();
        if ((int) lastKeyframe.getAgentDetail(VitalSign.PARAMETER_PREFIX + "age") >= (int) lastKeyframe.getAgentDetail(Agent.Evolvable.PARAMETER_PREFIX + "teenAge")) {
            stroke = new BasicStroke(2);
        }
        g2d.setStroke(stroke);
        Shape agentShape = createHerbivoreShape(tweenFrame);
        g2d.fill(agentShape);

        Color borderColor = computeBorderColor(lastKeyframe.getAgentDetails());
        g2d.setColor(borderColor);

        g2d.draw(agentShape);
    }

    private void drawVision(Graphics2D g2d, int distance, Position.Immutable position) {
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
    private Shape createHerbivoreShape(AgentKeyframe frame) {
        int size = frame.getAgentDetail("size");

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
