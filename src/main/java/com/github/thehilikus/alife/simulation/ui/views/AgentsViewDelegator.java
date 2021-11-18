package com.github.thehilikus.alife.simulation.ui.views;

import com.github.thehilikus.alife.agent.social.Herbivore;
import com.github.thehilikus.alife.agent.plants.Plant;
import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.world.Edge;
import com.github.thehilikus.alife.simulation.ui.AgentKeyframe;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

/**
 * The top class to represent agents in different media
 */
public class AgentsViewDelegator implements AgentView {
    private final Map<String, AgentView> agentsViews = Map.of(
            Edge.class.getSimpleName(), new EdgeView(),
            Plant.class.getSimpleName(), new PlantView(),
            Herbivore.class.getSimpleName(), new HerbivoreView()
    );

    @Override
    public void drawInConsole(StringBuilder stringBuilder, Agent agent) {
        AgentView view = agentsViews.get(agent.getClass().getSimpleName());
        Objects.requireNonNull(view, "No view found that can draw " + agent);
        view.drawInConsole(stringBuilder, agent);
    }

    @Override
    public Shape drawKeyframe(Graphics2D g2d, AgentKeyframe newKeyframe, boolean selected) {
        Stroke originalStroke = g2d.getStroke();

        AgentView view = agentsViews.get(newKeyframe.getAgentDetail("type"));
        Objects.requireNonNull(view, "No view found that can draw " + newKeyframe);
        Shape result = view.drawKeyframe(g2d, newKeyframe, selected);

        g2d.setStroke(originalStroke);

        return result;
    }

    @Override
    public void drawTweenFrame(Graphics2D g2d, AgentKeyframe lastKeyframe, AgentKeyframe newKeyframe, double percentToKeyframe) {
        Stroke originalStroke = g2d.getStroke();

        AgentView view = agentsViews.get(newKeyframe.getAgentDetail("type"));
        Objects.requireNonNull(view, "No view found that can draw " + newKeyframe);
        view.drawTweenFrame(g2d, lastKeyframe, newKeyframe, percentToKeyframe);

        g2d.setStroke(originalStroke);
    }

    @Override
    public AgentKeyframe createAgentFrame(Agent agent) {
        AgentView view = agentsViews.get(agent.getClass().getSimpleName());
        Objects.requireNonNull(view, "No view found that can draw " + agent);

        return view.createAgentFrame(agent);
    }
}
