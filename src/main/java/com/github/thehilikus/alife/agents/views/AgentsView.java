package com.github.thehilikus.alife.agents.views;

import com.github.thehilikus.alife.agents.animals.Herbivore;
import com.github.thehilikus.alife.agents.plants.Plant;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.world.Edge;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Map;
import java.util.Objects;

/**
 * The top class to represent agents in different media
 */
public class AgentsView implements Agent.View {
    /**
     * The maximum size for shapes in the UI
     */
    public final static int MAX_REPRESENTATION_SIZE = 25;
    /**
     * The minimum size for shapes in the UI
     */
    public final static int MIN_REPRESENTATION_SIZE = 3;

    private final Map<Class<? extends Agent>, Agent.View> agentsViews = Map.of(
            Edge.class, new EdgeView(),
            Plant.class, new PlantView(),
            Herbivore.class, new HerbivoreView()
    );

    @Override
    public void drawInConsole(StringBuilder stringBuilder, Agent agent) {
        Agent.View view = agentsViews.get(agent.getClass());
        Objects.requireNonNull(view, "No view found that can draw " + agent);
        view.drawInConsole(stringBuilder, agent);
    }

    @Override
    public Shape drawIn2DGraphics(Graphics2D g2d, Agent agent) {
        AffineTransform transform = g2d.getTransform();

        Agent.View view = agentsViews.get(agent.getClass());
        Objects.requireNonNull(view, "No view found that can draw " + agent);
        Shape result = view.drawIn2DGraphics(g2d, agent);

        g2d.setTransform(transform);

        return result;
    }
}
