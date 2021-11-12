package com.github.thehilikus.alife.agents.views;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.Position;
import com.github.thehilikus.alife.api.VitalSign;
import com.github.thehilikus.alife.world.ui.AgentKeyframe;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Map;
import java.util.Objects;

/**
 * Representations for {@link com.github.thehilikus.alife.agents.plants.Plant}
 */
public class PlantView implements Agent.View {

    private static final int Z_ORDER = 5;
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
    public AgentKeyframe createAgentFrame(Agent agent) {
        Map<String, Object> details = agent.getDetails();
        AgentKeyframe result = new AgentKeyframe(agent.getId(), Z_ORDER, details);
        result.addPropertyToInterpolate("energy", details.get(VitalSign.PARAMETER_PREFIX + "energy"));
        String moodName = details.get(Mood.PARAMETER_PREFIX + "current").toString();
        Color moodColor = graphicalMoodColours.get(moodName);
        Objects.requireNonNull(moodColor, "Mood color was empty for " + moodName);
        result.addPropertyToInterpolate("color", moodColor);

        return result;
    }

    @Override
    public Shape drawKeyframe(Graphics2D g2d, AgentKeyframe newKeyframe, boolean selected) {
        Shape agentShape = createPlantShape(newKeyframe);

        Color moodColor = newKeyframe.getInterpolatedProperty("color");
        g2d.setColor(moodColor);
        g2d.fill(agentShape);
        g2d.setColor(Color.BLACK);
        g2d.draw(agentShape);

        return agentShape;
    }

    private Shape createPlantShape(AgentKeyframe frame) {
        int agentSize = (int) frame.getAgentDetails().get("size");

        double vitality = ((Integer) frame.getInterpolatedProperty("energy")).doubleValue() / EnergyTracker.MAX_ENERGY;
        //noinspection MagicNumber
        double radius = agentSize / 2.0 * vitality;

        int x = ((Position.Immutable) frame.getAgentDetails().get("position")).getX();
        int y = ((Position.Immutable) frame.getAgentDetails().get("position")).getY();
        return new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2);
    }

    @Override
    public void drawTweenFrame(Graphics2D g2d, AgentKeyframe lastKeyframe, AgentKeyframe newKeyframe, double percentToKeyframe) {
        AgentKeyframe tweenFrame = lastKeyframe.interpolate(newKeyframe, percentToKeyframe);

        Shape agentShape = createPlantShape(tweenFrame);
        Color agentColor = tweenFrame.getInterpolatedProperty("color");
        g2d.setColor(agentColor);
        g2d.fill(agentShape);
        g2d.setColor(Color.BLACK);
        g2d.draw(agentShape);
    }
}
