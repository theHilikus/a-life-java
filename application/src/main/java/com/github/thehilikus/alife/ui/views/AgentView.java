package com.github.thehilikus.alife.ui.views;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.ui.AgentKeyframe;

import java.awt.*;

/**
 * A class that knows how to present agents in a UI
 */
public interface AgentView {
    void drawInConsole(StringBuilder builder, Agent agent);

    AgentKeyframe createAgentFrame(Agent agent);

    Shape drawKeyframe(Graphics2D g2d, AgentKeyframe newKeyframe, boolean selected);

    void drawTweenFrame(Graphics2D g2d, AgentKeyframe lastKeyframe, AgentKeyframe newKeyframe, double percentToKeyframe);
}
