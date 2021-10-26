package com.github.thehilikus.alife.agents.views;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.Agent;

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
    public Shape drawIn2DGraphics(Graphics2D g2d, Agent agent) {
        throw new UnsupportedOperationException("Edge is not drawn in graphical view");
    }
}
