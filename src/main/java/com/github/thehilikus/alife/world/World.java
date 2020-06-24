package com.github.thehilikus.alife.world;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Position;
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
        grid = new Agent[width + 2][height + 2];
        createEdge();
        day = 0;
    }

    private void createEdge() {
        int x = 0;
        while (x < getWidth()) {
            grid[0][x] = new Edge();
            grid[getHeight() - 1][x] = new Edge();
            x++;
        }

        int y = 0;
        while (y < getHeight()) {
            grid[y][0] = new Edge();
            grid[y][getWidth() - 1] = new Edge();
            y++;
        }
    }

    private int getWidth() {
        return grid.length;
    }

    private int getHeight() {
        return grid[0].length;
    }

    public void tick() {
        LOG.info("Starting day {}", ++day);
        agents.values().forEach(agent -> {
            Position originalPosition = agent.getPosition();
            grid[originalPosition.getY()][originalPosition.getX()] = null;
            boolean alive = agent.tick();
            if (alive) {
                Position newPosition = agent.getPosition();
                grid[newPosition.getY()][newPosition.getX()] = agent;
                if (!originalPosition.equals(newPosition)) {
                    LOG.debug("Moved {} from {} to {}", agent, originalPosition, newPosition);
                }
            } else {
                LOG.debug("{} died", agent);
                removeAgent(agent);
            }
        });
    }

    public void addAgent(Agent agent) {
        Position position = agent.getPosition();
        LOG.info("Adding {} to world", agent);
        grid[position.getY()][position.getX()] = agent;
        agents.put(agent.getId(), agent);
    }

    private void removeAgent(Agent agent) {
        Position position = agent.getPosition();
        LOG.info("Removing {} from world", agent);
        grid[position.getY()][position.getX()] = null;
        agents.remove(agent.getId());
    }

    public Agent getAgent(int id) {
        return agents.get(id);
    }

    public Position getEmptyPosition() {
        int x;
        int y;
        do {
            x = RandomSource.nextInt(1, getWidth() - 1);
            y = RandomSource.nextInt(1, getHeight() - 1);
        } while (grid[x][y] != null);

        return new Position(x, y);
    }

    public Agent getObjectRelativeTo(int id, int xDelta, int yDelta) {
        Position center = agents.get(id).getPosition();
        int x = center.getX() + xDelta;
        int y = center.getY() + yDelta;
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return null;
        }

        return grid[y][x];
    }

    public Map<String, String> getAgentDetails(int agentId) {
        Map<String, String> result = Collections.emptyMap();
        Agent agent = agents.get(agentId);
        if (agent != null) {
            result = agent.getDetails();
        }

        return result;
    }

    public String getRepresentation() {
        StringBuilder stringBuilder = new StringBuilder(getWidth() * getHeight() * 2);
        stringBuilder.append("World view on day ").append(day).append(System.lineSeparator());

        String formatCode = Ansi.generateCode(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE);
        String emptySpace = Ansi.formatMessage("  ", formatCode);
        for (int y = 0; y < getWidth(); y++) {
            for (int x = 0; x < getHeight(); x++) {
                Agent agent = grid[y][x];
                if (agent == null) {
                    stringBuilder.append(emptySpace);
                } else {
                    stringBuilder.append(agent.getStringRepresentation());
                }
            }
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }
}
