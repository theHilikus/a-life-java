package com.github.thehilikus.alife.ui.views;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.ui.AgentKeyframe;
import com.github.thehilikus.alife.world.Edge;

import java.awt.*;
import java.util.Map;

/**
 * Representations for {@link Edge}
 */
public class EdgeView implements AgentView {
    @Override
    public void drawInConsole(StringBuilder builder, Map<String, Object> agentDetails) {
        String formatCode = Ansi.generateCode(Ansi.Attribute.NONE, Ansi.FColor.WHITE, Ansi.BColor.WHITE);
        builder.append(Ansi.formatMessage("  ", formatCode));
    }

    @Override
    public AgentKeyframe createAgentFrame(Map<String, Object> agentDetails) {
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
