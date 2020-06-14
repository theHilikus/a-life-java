package com.github.thehilikus.alife.world;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Coordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The environment where the agents live
 */
public class World {
    private static final Logger LOG = LoggerFactory.getLogger(World.class.getSimpleName());
    private final Agent[][] grid;
    private final Map<Integer, Agent> agents = new HashMap<>();
    private int day;

    public World(int width, int height) {
        grid = new Agent[width][height];
        day = 0;
    }

    public int getWidth() {
        return grid.length;
    }

    public int getHeight() {
        return grid[0].length;
    }

    public void tick() {
        LOG.info("Starting day {}", ++day);
        agents.values().forEach(agent -> {
            Coordinates.Immutable originalPosition = agent.getPosition();
            grid[originalPosition.getX()][originalPosition.getY()] = null;
            agent.tick();
            Coordinates.Immutable newPosition = agent.getPosition();
            grid[newPosition.getX()][newPosition.getY()] = agent;
            if (!originalPosition.equals(newPosition)) {
                LOG.debug("Moved {} from {} to {}", agent, originalPosition, newPosition);
            }
        });
    }

    public void addAgent(Agent agent) {
        Coordinates.Immutable position = agent.getPosition();
        LOG.info("Adding {} to world at position {}", agent, position);
        grid[position.getX()][position.getY()] = agent;
        agents.put(agent.getId(), agent);
    }

    public void removeAgent(Agent agent) {
        agents.remove(agent.getId());
    }

    public Coordinates getEmptyPosition() {
        int x;
        int y;
        do {
            x = RandomSource.nextInt(getWidth());
            y = RandomSource.nextInt(getHeight());
        } while (grid[x][y] != null);

        return new Coordinates(x, y);
    }

    public Agent getObjectRelativeTo(int id, int xDelta, int yDelta) {
        Coordinates.Immutable center = agents.get(id).getPosition();
        int x = center.getX() + xDelta;
        int y = center.getY() + yDelta;
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return null;
        }

        return grid[x][y];
    }

    public Map<String, String> getAgentDetails(int agentId) {
        Map<String, String> result = Collections.emptyMap();
        Agent agent = agents.get(agentId);
        if (agent != null) {
            result = agent.getParameters();
        }

        return result;
    }

    public String getRepresentation() {
        StringBuilder stringBuilder = new StringBuilder(getWidth() * getHeight() * 2);
        stringBuilder.append("World view on day ").append(day).append(System.lineSeparator());

        String formatCode = Ansi.generateCode(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE);
        String formatCodeEdge = Ansi.generateCode(Ansi.Attribute.NONE, Ansi.FColor.WHITE, Ansi.BColor.WHITE);
        String emptySpace = Ansi.formatMessage("  ", formatCode);
        String emptySpaceEdge = Ansi.formatMessage("  ", formatCodeEdge);
        for (int row = 0; row < getWidth(); row++) {
            for (int col = 0; col < getHeight(); col++) {
                Agent agent = grid[row][col];
                if (agent == null) {
                    if (row == 0 || col == 0 || row == getWidth() -1 || col == getHeight() -1) {
                     stringBuilder.append(" ").append(emptySpaceEdge);
                    } else {
                        stringBuilder.append(" ").append(emptySpace);
                    }
                } else {
                    stringBuilder.append(" ").append(agent.getStringRepresentation());
                }
            }
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }
}
