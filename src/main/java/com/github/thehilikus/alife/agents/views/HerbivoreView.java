package com.github.thehilikus.alife.agents.views;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.controllers.HungerTracker;
import com.github.thehilikus.alife.api.*;

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
    public Shape drawIn2DGraphics(Graphics2D g2d, Agent agent, boolean selected) {
        Map<String, Object> details = agent.getDetails();

        if (selected) {
            drawVision(g2d, details, agent.getPosition());
        }

        Color agentColor = computeAgentColor(details);
        g2d.setColor(agentColor);

        if ((int) details.get(VitalSign.PARAMETER_PREFIX + "age") >= (int) details.get(Agent.Evolvable.PARAMETER_PREFIX + "teenAge")) {
            g2d.setStroke(new BasicStroke(2));
        }

        Shape agentShape = createHerbivoreShape(details, agent.getPosition());
        g2d.fill(agentShape);

        Color borderColor = computeBorderColor(details);
        g2d.setColor(borderColor);

        g2d.draw(agentShape);

        return agentShape;
    }

    private void drawVision(Graphics2D g2d, Map<String, Object> details, Position.Immutable position) {
        int distance = (int) details.get(Vision.PARAMETER_PREFIX + "distance");
        int x = position.getX() - distance / 2;
        int y = position.getY() - distance / 2;
        Shape vision = new Rectangle(x, y, distance, distance);

        g2d.setColor(Color.BLUE);
        g2d.draw(vision);
    }

    @SuppressWarnings("MagicNumber")
    private Shape createHerbivoreShape(Map<String, Object> details, Position.Immutable position) {
        int agentSize = (int) details.get("size");
        Orientation direction = (Orientation) details.get(Locomotion.PARAMETER_PREFIX + "orientation");

        Path2D triangle = new Path2D.Double();
        triangle.moveTo(-representationSize, 2.5 * representationSize / 3);
        triangle.lineTo(representationSize, 2.5 * representationSize / 3);
        triangle.lineTo(0, -5.0 * representationSize / 3);
        triangle.closePath();

        AffineTransform transform = new AffineTransform();
        transform.translate(position.getX(), position.getY());
        transform.rotate(direction.toRadians());
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
