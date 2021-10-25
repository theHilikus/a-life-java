package com.github.thehilikus.alife.agents.views;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.agents.controllers.HungerTracker;
import com.github.thehilikus.alife.api.*;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Map;
import java.util.Objects;

/**
 * Representations for {@link com.github.thehilikus.alife.agents.animals.Herbivore}
 */
public class HerbivoreView implements Agent.View {

    private final Map<String, Ansi.FColor> consoleMoodColours = Map.of(
            "Eating", Ansi.FColor.GREEN,
            "Existing", Ansi.FColor.WHITE,
            "Hunting", Ansi.FColor.BLUE,
            "InHeat", Ansi.FColor.WHITE,
            "InHeatChasing", Ansi.FColor.MAGENTA,
            "Mating", Ansi.FColor.RED,
            "Scouting", Ansi.FColor.CYAN,
            "Sleeping", Ansi.FColor.YELLOW
    );

    private final Map<String, Color> graphicalMoodColours = Map.of(
            "Eating", Color.GREEN,
            "Existing", Color.GRAY,
            "Hunting", Color.BLUE,
            "InHeat", Color.PINK,
            "InHeatChasing", Color.MAGENTA,
            "Mating", Color.RED,
            "Scouting", Color.CYAN,
            "Sleeping", Color.LIGHT_GRAY
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
    public void drawIn2DGraphics(Graphics2D g2d, Agent agent) {
        Map<String, Object> details = agent.getDetails();

        int agentSize = (int) details.get("size");
        double representationSize = Math.max((double) agentSize / Agent.Living.MAX_SIZE * AgentsView.MAX_REPRESENTATION_SIZE, AgentsView.MIN_REPRESENTATION_SIZE);
        Shape agentShape = createHerbivoreShape(representationSize);

        String moodName = details.get(Mood.PARAMETER_PREFIX + "current").toString();
        Color moodColor = graphicalMoodColours.get(moodName);
        Objects.requireNonNull(moodColor, "Mood color was empty for " + moodName);
        float[] rgbColorComponents = moodColor.getRGBColorComponents(null);
        float vitality = calculateVitality(details);
        Color agentColor = new Color(rgbColorComponents[0], rgbColorComponents[1], rgbColorComponents[2], vitality);

        double direction = ((Orientation) details.get(Locomotion.PARAMETER_PREFIX + "orientation")).toRadians();

        g2d.translate(agent.getPosition().getX(), agent.getPosition().getY());
        g2d.rotate(direction);
        g2d.setColor(agentColor);
        g2d.fill(agentShape);
        g2d.setColor(Color.BLACK);
        g2d.draw(agentShape);
    }

    private float calculateVitality(Map<String, Object> details) {
        float energy = (int) details.get(VitalSign.PARAMETER_PREFIX + "energy") / (float) EnergyTracker.MAX_ENERGY;
        float hunger = (int) details.get(VitalSign.PARAMETER_PREFIX + "hunger") / (float) HungerTracker.FULL_THRESHOLD;

        return Math.min(energy, hunger);
    }

    private Path2D createHerbivoreShape(double representationSize) {
        Path2D triangle = new Path2D.Double();
        triangle.moveTo(-representationSize, 2.5 * representationSize / 3);
        triangle.lineTo(representationSize, 2.5 * representationSize / 3);
        triangle.lineTo(0, -5.0 * representationSize / 3);
        triangle.closePath();

        return triangle;
    }
}
