package com.github.thehilikus.alife.agents.plants;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Position;
import com.github.thehilikus.alife.world.IdsSource;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An inanimate agent that gets eaten
 */
public class Plant implements Agent.Living {
    private static final Logger LOG = LoggerFactory.getLogger(Plant.class.getSimpleName());
    private final int id;
    private final Position position;

    public static void create(int count, World world) {
        for (int current = 0; current < count; current++) {
            int id = IdsSource.getNextId();
            Position startingPosition = world.getEmptyPosition();

            Agent.Living newAgent = new Plant(id, startingPosition);
            LOG.info("Created {}", newAgent);
            world.addAgent(newAgent);
        }
    }

    private Plant(int id, Position position) {
        this.id = id;
        this.position = position;
    }

    @Override
    public boolean tick() {
        LOG.debug("#### Updating state of {} ####", this);
        //TODO: implement energy calculation
        return true;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Map<String, String> getDetails() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("type", getClass().getSimpleName());
        result.put("position", position.getX() + ", " + position.getY());

        return result;
    }

    @Override
    public Position.Immutable getPosition() {
        return position.toImmutable();
    }

    @Override
    public String getStringRepresentation() {
        Ansi.Attribute agentTypeStyle = Ansi.Attribute.NONE;

        Ansi.BColor background = Ansi.BColor.GREEN;
        String formatCode = Ansi.generateCode(agentTypeStyle, Ansi.FColor.WHITE, background);
        String idString = Integer.toString(id);
        if (id < 10) {
            idString = ' ' + idString;
        }
        return Ansi.formatMessage(idString, formatCode);
    }

    @Override
    public String toString() {
        return "Plant{" +
                "id=" + id +
                ", position=" + position +
                '}';
    }
}
