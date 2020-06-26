package com.github.thehilikus.alife.agents.plants;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.animals.Herbivore;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.DaggerLivingAgentComponent;
import com.github.thehilikus.alife.api.LivingAgentComponent;
import com.github.thehilikus.alife.api.Position;
import com.github.thehilikus.alife.world.WorldComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An inanimate agent that gets eaten
 */
public class Plant implements Agent.Living {
    private static final Logger LOG = LoggerFactory.getLogger(Plant.class.getSimpleName());
    private final int id;
    private final Position position;

    public static void create(int count, WorldComponent worldComponent) {
        for (int current = 0; current < count; current++) {
            LivingAgentComponent livingAgentComponent = DaggerLivingAgentComponent.builder().worldComponent(worldComponent).build();
            Agent.Living newAgent = livingAgentComponent.createPlant();
            LOG.info("Created {}", newAgent);
            worldComponent.createWorld().addAgent(newAgent);
        }
    }

    @Inject
    public Plant(int id, Position position) {
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
