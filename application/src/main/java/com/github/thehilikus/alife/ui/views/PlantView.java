package com.github.thehilikus.alife.ui.views;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agent.api.AgentDetails;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
import com.github.thehilikus.alife.ui.AgentKeyframe;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Map;
import java.util.Objects;

/**
 * Representations for Plants
 */
public class PlantView implements AgentView {

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
            "Growing", Color.GREEN,
            "Pollinating", Color.PINK
    );

    @Override
    public void drawInConsole(StringBuilder builder, AgentDetails.Immutable agentDetails) {
        Ansi.Attribute agentTypeStyle = Ansi.Attribute.NONE;

        String moodName = agentDetails.getAttribute(Mood.PARAMETER_PREFIX + "current").toString();
        Ansi.FColor moodColour = consoleMoodColours.get(moodName);
        Objects.requireNonNull(moodColour, "Console mood colour was empty for " + moodName);
        Ansi.BColor background = Ansi.BColor.GREEN;
        String formatCode = Ansi.generateCode(agentTypeStyle, moodColour, background);

        String idString = Integer.toString(agentDetails.getId());
        if (agentDetails.getId() < 10) {
            idString = ' ' + idString;
        }

        builder.append(Ansi.formatMessage(idString, formatCode));
    }

    @Override
    public AgentKeyframe createAgentFrame(AgentDetails.Immutable agentDetails) {
        AgentKeyframe result = new AgentKeyframe(agentDetails.getId(), Z_ORDER, agentDetails);
        result.addPropertyToInterpolate("size", agentDetails.getAttribute("size"));
        String moodName = agentDetails.getAttribute(Mood.PARAMETER_PREFIX + "current").toString();
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
        int maxSize = frame.getAgentDetail(VitalSign.PARAMETER_PREFIX + "maxSize");

        double vitality = ((Integer) frame.getInterpolatedProperty("size")).doubleValue() / maxSize;
        //noinspection MagicNumber
        double radius = maxSize / 2.0 * vitality;

        int x = ((Position.Immutable) frame.getAgentDetail("position")).getX();
        int y = ((Position.Immutable) frame.getAgentDetail("position")).getY();
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
