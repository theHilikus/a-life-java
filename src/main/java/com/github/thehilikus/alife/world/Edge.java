package com.github.thehilikus.alife.world;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Position;

import java.util.Collections;
import java.util.Map;

/**
 * An agent that represents a side limit of the world
 */
public class Edge implements Agent {
    private final Position.Immutable position;

    public Edge(Position.Immutable position) {
        this.position = position;
    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public Map<String, String> getDetails() {
        return Collections.emptyMap();
    }

    @Override
    public Position.Immutable getPosition() {
        return position;
    }

    @Override
    public String getStringRepresentation() {
        String formatCode = Ansi.generateCode(Ansi.Attribute.NONE, Ansi.FColor.WHITE, Ansi.BColor.WHITE);
        return Ansi.formatMessage("  ", formatCode);
    }
}
