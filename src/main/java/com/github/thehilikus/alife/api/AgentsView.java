package com.github.thehilikus.alife.api;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Map;
import java.util.Objects;

/**
 * A class in charge of drawing different agents
 */
public class AgentsView {
    private final static int SIZE = 6;
    private final static Map<String, Shape> shapes = Map.of(
            "Herbivore", createHerbivoreShape(),
            "Plant", new Ellipse2D.Double(0, 0, 2 * SIZE, 2 * SIZE)
    );


    @SuppressWarnings("MagicNumber")
    private static Path2D createHerbivoreShape() {
        Path2D triangle = new Path2D.Double();
        triangle.moveTo(-SIZE, 2.5 * SIZE / 3);
        triangle.lineTo(SIZE, 2.5 * SIZE / 3);
        triangle.lineTo(0, -5.0 * SIZE / 3);
        triangle.closePath();

        return triangle;
    }

    private final Map<String, Color> moodColours = Map.of(
            "Eating", Color.GREEN,
            "Existing", Color.BLACK,
            "Hunting", Color.BLUE,
            "InHeat", Color.PINK,
            "InHeatChasing", Color.MAGENTA,
            "Mating", Color.RED,
            "Scouting", Color.CYAN,
            "Sleeping", Color.LIGHT_GRAY,
            "BeingEaten", Color.YELLOW,
            "Growing", Color.GREEN
    );

    public void draw(Graphics2D g2d, Agent agent) {
        AffineTransform transform = g2d.getTransform();

        String agentType = agent.getClass().getSimpleName();
        Shape agentShape = shapes.get(agentType);
        Objects.requireNonNull(agentShape, "Agent shape was empty for " + agentType);
        String moodName = agent.getDetails().get(Mood.PARAMETER_PREFIX + "current");
        Color moodColor = moodColours.get(moodName);
        Objects.requireNonNull(moodColor, "Mood color was empty for " + moodName);

        g2d.translate(agent.getPosition().getX(), agent.getPosition().getY());
        g2d.rotate(agent.getOrientation().toRadians());
        g2d.setColor(moodColor);
        g2d.fill(agentShape);
        g2d.setColor(Color.BLACK);
        g2d.draw(agentShape);

        g2d.setTransform(transform);
    }
}
