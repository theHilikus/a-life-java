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
    public Shape drawIn2DGraphics(Graphics2D g2d, Agent agent) {
        Map<String, Object> details = agent.getDetails();

        int agentSize = (int) details.get("size");
        double representationSize = Math.max((double) agentSize / Agent.Living.MAX_SIZE * AgentsView.MAX_REPRESENTATION_SIZE, AgentsView.MIN_REPRESENTATION_SIZE);
        Orientation direction = (Orientation) details.get(Locomotion.PARAMETER_PREFIX + "orientation");
        Shape agentShape = createHerbivoreShape(agent.getPosition().getX(), agent.getPosition().getY(), direction, representationSize);

        String moodName = details.get(Mood.PARAMETER_PREFIX + "current").toString();
        Color moodColor = graphicalMoodColours.get(moodName);
        Objects.requireNonNull(moodColor, "Mood color was empty for " + moodName);
        float[] rgbColorComponents = moodColor.getRGBColorComponents(null);
        float vitality = calculateVitality(details);
        Color agentColor = new Color(rgbColorComponents[0], rgbColorComponents[1], rgbColorComponents[2], vitality);

        if ((int) details.get(VitalSign.PARAMETER_PREFIX + "age") >= (int) details.get(Agent.Evolvable.PARAMETER_PREFIX + "teenAge")) {
            g2d.setStroke(new BasicStroke(2));
        }

        g2d.setColor(agentColor);
        g2d.fill(agentShape);
        int lifeExpectancy = (int) details.get(VitalSign.PARAMETER_PREFIX + "lifeExpectancy");
        double lifeLeftProportion = (lifeExpectancy - (int) details.get(VitalSign.PARAMETER_PREFIX + "age")) / (double) lifeExpectancy;
        if (lifeLeftProportion < OLD_AGE_PROPORTION) {
            g2d.setColor(Color.LIGHT_GRAY);
        } else {
            g2d.setColor(Color.BLACK);
        }

        g2d.draw(agentShape);

        return agentShape;
    }

    private float calculateVitality(Map<String, Object> details) {
        float energy = (int) details.get(VitalSign.PARAMETER_PREFIX + "energy") / (float) EnergyTracker.MAX_ENERGY;
        float hunger = (int) details.get(VitalSign.PARAMETER_PREFIX + "hunger") / (float) HungerTracker.FULL_THRESHOLD;

        return Math.min(energy, hunger);
    }

    private Path2D createHerbivoreShape(int x, int y, Orientation direction, double representationSize) {
        Path2D triangle = new Path2D.Double();
        triangle.moveTo(-representationSize, 2.5 * representationSize / 3);
        triangle.lineTo(representationSize, 2.5 * representationSize / 3);
        triangle.lineTo(0, -5.0 * representationSize / 3);
        triangle.closePath();


        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(direction.toRadians());
        triangle.transform(transform);

        return triangle;
    }
}
