package com.github.thehilikus.alife.agents.views;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.VitalSign;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Map;
import java.util.Objects;

/**
 * Representations for {@link com.github.thehilikus.alife.agents.plants.Plant}
 */
public class PlantView implements Agent.View {

    private final Map<String, Ansi.FColor> consoleMoodColours = Map.of(
            "BeingEaten", Ansi.FColor.YELLOW,
            "Growing", Ansi.FColor.BLACK
    );

    /**
     * Mapping of moods to colours
     */
    public static final Map<String, Color> graphicalMoodColours = Map.of(
            "BeingEaten", Color.YELLOW,
            "Growing", Color.GREEN
    );

    @Override
    public void drawInConsole(StringBuilder builder, Agent agent) {
        Ansi.Attribute agentTypeStyle = Ansi.Attribute.NONE;

        Map<String, Object> details = agent.getDetails();
        String moodName = details.get(Mood.PARAMETER_PREFIX + "current").toString();
        Ansi.FColor moodColour = consoleMoodColours.get(moodName);
        Objects.requireNonNull(moodColour, "Console mood colour was empty for " + moodName);
        Ansi.BColor background = Ansi.BColor.GREEN;
        String formatCode = Ansi.generateCode(agentTypeStyle, moodColour, background);

        int id = agent.getId();
        String idString = Integer.toString(id);
        if (id < 10) {
            idString = ' ' + idString;
        }

        builder.append(Ansi.formatMessage(idString, formatCode));
    }

    @Override
    public Shape drawIn2DGraphics(Graphics2D g2d, Agent plant, boolean selected) {
        Map<String, Object> details = plant.getDetails();

        int agentSize = (int) details.get("size");
        double representationSize = Math.max((double) agentSize / Agent.Living.MAX_SIZE * AgentsView.MAX_REPRESENTATION_SIZE, AgentsView.MIN_REPRESENTATION_SIZE);
        Shape agentShape = new Ellipse2D.Double(plant.getPosition().getX(), plant.getPosition().getY(), representationSize * 2, representationSize * 2);

        String moodName = details.get(Mood.PARAMETER_PREFIX + "current").toString();
        Color moodColor = graphicalMoodColours.get(moodName);
        Objects.requireNonNull(moodColor, "Mood color was empty for " + moodName);
        float[] rgbColorComponents = moodColor.getRGBColorComponents(null);
        float vitality = ((Integer) details.get(VitalSign.PARAMETER_PREFIX + "energy")).floatValue() / EnergyTracker.MAX_ENERGY;
        Color plantColor = new Color(rgbColorComponents[0], rgbColorComponents[1], rgbColorComponents[2], vitality);

        g2d.setColor(plantColor);
        g2d.fill(agentShape);
        g2d.setColor(Color.BLACK);
        g2d.draw(agentShape);

        return agentShape;
    }

}
