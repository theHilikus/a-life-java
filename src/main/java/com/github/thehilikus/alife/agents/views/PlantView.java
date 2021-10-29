package com.github.thehilikus.alife.agents.views;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.controllers.EnergyTracker;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Mood;
import com.github.thehilikus.alife.api.Position;
import com.github.thehilikus.alife.api.VitalSign;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
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

    private final Map<Agent, Frame> lastKeyframes = new HashMap<>();

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
    public Shape drawKeyframe(Graphics2D g2d, Agent plant, boolean selected) {
        Map<String, Object> details = plant.getDetails();

        Frame keyframe = new Frame();
        keyframe.addFixedProperty("size", details.get("size"));
        keyframe.addPropertyToInterpolate("energy", details.get(VitalSign.PARAMETER_PREFIX + "energy"));
        keyframe.addFixedProperty("position", plant.getPosition());

        Shape agentShape = createPlantShape(keyframe);

        String moodName = details.get(Mood.PARAMETER_PREFIX + "current").toString();
        Color moodColor = graphicalMoodColours.get(moodName);
        Objects.requireNonNull(moodColor, "Mood color was empty for " + moodName);
        keyframe.addPropertyToInterpolate("color", moodColor);

        g2d.setColor(moodColor);
        g2d.fill(agentShape);
        g2d.setColor(Color.BLACK);
        g2d.draw(agentShape);

        lastKeyframes.put(plant, keyframe);

        return agentShape;
    }

    private Shape createPlantShape(Frame frame) {
        int agentSize = frame.getFixedProperty("size");

        double vitality = ((Integer) frame.getInterpolatedProperty("energy")).doubleValue() / EnergyTracker.MAX_ENERGY;
        //noinspection MagicNumber
        double radius = agentSize / 2.0 * vitality;

        int x = ((Position.Immutable) frame.getFixedProperty("position")).getX();
        int y = ((Position.Immutable) frame.getFixedProperty("position")).getY();
        return new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2);
    }

    @Override
    public void drawTweenFrame(Graphics2D g2d, Agent agent, double percentToKeyFrame) {
        Frame previousKeyframe = lastKeyframes.get(agent);
        Frame newKeyframe = new Frame();
        newKeyframe.addPropertyToInterpolate("energy", agent.getDetails().get(VitalSign.PARAMETER_PREFIX + "energy"));
        String moodName = agent.getDetails().get(Mood.PARAMETER_PREFIX + "current").toString();
        Color moodColor = graphicalMoodColours.get(moodName);
        newKeyframe.addPropertyToInterpolate("color", moodColor);

        Frame tweenFrame = previousKeyframe.interpolate(newKeyframe, percentToKeyFrame);

        Shape agentShape = createPlantShape(tweenFrame);
        Color agentColor = tweenFrame.getInterpolatedProperty("color");
        g2d.setColor(agentColor);
        g2d.fill(agentShape);
        g2d.setColor(Color.BLACK);
        g2d.draw(agentShape);
    }

}
