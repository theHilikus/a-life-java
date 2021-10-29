package com.github.thehilikus.alife.agents.views;

import com.github.thehilikus.alife.agents.animals.Herbivore;
import com.github.thehilikus.alife.agents.plants.Plant;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.world.Edge;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

/**
 * The top class to represent agents in different media
 */
public class AgentsView implements Agent.View {
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
    public Shape drawKeyframe(Graphics2D g2d, Agent agent, boolean selected) {
        Stroke originalStroke = g2d.getStroke();

        Agent.View view = agentsViews.get(agent.getClass());
        Objects.requireNonNull(view, "No view found that can draw " + agent);
        Shape result = view.drawKeyframe(g2d, agent, selected);

        g2d.setStroke(originalStroke);

        return result;
    }

    @Override
    public void drawTweenFrame(Graphics2D g2d, Agent agent, double percentToKeyFrame) {
        Stroke originalStroke = g2d.getStroke();

        Agent.View view = agentsViews.get(agent.getClass());
        Objects.requireNonNull(view, "No view found that can draw " + agent);
        view.drawTweenFrame(g2d, agent, percentToKeyFrame);

        g2d.setStroke(originalStroke);
    }
}
