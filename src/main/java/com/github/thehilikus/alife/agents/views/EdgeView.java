package com.github.thehilikus.alife.agents.views;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.world.ui.AgentKeyframe;

import java.awt.*;

/**
 * Representations for {@link com.github.thehilikus.alife.world.Edge}
 */
public class EdgeView implements Agent.View {
    @Override
    public void drawInConsole(StringBuilder builder, Agent agent) {
        String formatCode = Ansi.generateCode(Ansi.Attribute.NONE, Ansi.FColor.WHITE, Ansi.BColor.WHITE);
        builder.append(Ansi.formatMessage("  ", formatCode));
    }

    @Override
    public AgentKeyframe createAgentFrame(Agent agent) {
        throw new UnsupportedOperationException("Edge is not drawn in graphical view");
    }

    @Override
    public Shape drawKeyframe(Graphics2D g2d, AgentKeyframe newKeyframe, boolean selected) {
        throw new UnsupportedOperationException("Edge is not drawn in graphical view");
    }

    @Override
    public void drawTweenFrame(Graphics2D g2d, AgentKeyframe lastKeyframe, AgentKeyframe newKeyframe, double percentToKeyframe) {
        throw new UnsupportedOperationException("Edge is not drawn in graphical view");
    }
}
