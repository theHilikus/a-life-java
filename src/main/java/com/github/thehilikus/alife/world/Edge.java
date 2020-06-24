package com.github.thehilikus.alife.world;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Position;

import java.util.Map;

/**
 * An agent that represents a side limit of the world
 */
public class Edge implements Agent {
    @Override
    public int getId() {
        return 0;
    }

    @Override
    public Map<String, String> getDetails() {
        return null;
    }

    @Override
    public boolean tick() {
        return true;
    }

    @Override
    public Position getPosition() {
        return null;
    }

    @Override
    public String getStringRepresentation() {
        String formatCode = Ansi.generateCode(Ansi.Attribute.NONE, Ansi.FColor.WHITE, Ansi.BColor.WHITE);
        return Ansi.formatMessage("  ", formatCode);
    }

    @Override
    public Genome getGenome() {
        return null;
    }
}
