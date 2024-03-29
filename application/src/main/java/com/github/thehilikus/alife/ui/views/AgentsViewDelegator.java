package com.github.thehilikus.alife.ui.views;

import com.github.thehilikus.alife.agent.api.AgentDetails;
import com.github.thehilikus.alife.ui.AgentKeyframe;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

/**
 * The top class to represent agents in different media
 */
public class AgentsViewDelegator implements AgentView {
    private final Map<String, AgentView> agentsViews = Map.of(
            "Edge", new EdgeView(),
            "Plant", new PlantView(),
            "Herbivore", new HerbivoreView()
    );

    @Override
    public void drawInConsole(StringBuilder stringBuilder, AgentDetails.Immutable agentDetails) {
        String agentType = agentDetails.getType();
        AgentView view = agentsViews.get(agentType);
        Objects.requireNonNull(view, "No view found that can draw " + agentType);
        view.drawInConsole(stringBuilder, agentDetails);
    }

    @Override
    public Shape drawKeyframe(Graphics2D g2d, AgentKeyframe newKeyframe, boolean selected) {
        Stroke originalStroke = g2d.getStroke();

        AgentView view = agentsViews.get((String) newKeyframe.getAgentDetail("type"));
        Objects.requireNonNull(view, "No view found that can draw " + newKeyframe);
        Shape result = view.drawKeyframe(g2d, newKeyframe, selected);

        g2d.setStroke(originalStroke);

        return result;
    }

    @Override
    public void drawTweenFrame(Graphics2D g2d, AgentKeyframe lastKeyframe, AgentKeyframe newKeyframe, double percentToKeyframe) {
        Stroke originalStroke = g2d.getStroke();

        AgentView view = agentsViews.get((String) newKeyframe.getAgentDetail("type"));
        Objects.requireNonNull(view, "No view found that can draw " + newKeyframe);
        view.drawTweenFrame(g2d, lastKeyframe, newKeyframe, percentToKeyframe);

        g2d.setStroke(originalStroke);
    }

    @Override
    public AgentKeyframe createAgentFrame(AgentDetails.Immutable agentDetails) {
        AgentView view = agentsViews.get(agentDetails.getType());
        Objects.requireNonNull(view, "No view found that can draw " + agentDetails);

        return view.createAgentFrame(agentDetails);
    }
}
