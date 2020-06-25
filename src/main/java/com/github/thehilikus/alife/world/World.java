package com.github.thehilikus.alife.world;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Orientation;
import com.github.thehilikus.alife.api.Position;
import dagger.Module;
import dagger.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.*;

/**
 * The environment where the agents live
 */
@Module
public class World {
    private static final Logger LOG = LoggerFactory.getLogger(World.class.getSimpleName());
    private final Agent[][] grid;
    private final Map<Integer, Agent.Living> agents = new HashMap<>();
    private int day;

    @Provides
    @Singleton
    static World provideWorld() {
        return new World(100, 100);
    }

    public World(int width, int height) {
        grid = new Agent[width + 2][height + 2];
        createEdge();
        day = 0;
    }

    private void createEdge() {
        int x = 0;
        while (x < getWidth()) {
            Position.Immutable position = new Position(x, 0).toImmutable();
            grid[position.getY()][position.getX()] = new Edge(position);

            position = new Position(x, getHeight() - 1).toImmutable();
            grid[position.getY()][position.getX()] = new Edge(position);
            x++;
        }

        int y = 0;
        while (y < getHeight()) {
            Position.Immutable position = new Position(0, y).toImmutable();
            grid[position.getY()][position.getX()] = new Edge(position);

            position = new Position(getWidth() - 1, y).toImmutable();
            grid[position.getY()][position.getX()] = new Edge(position);
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
        Collection<Agent> cemetery = new HashSet<>();
        agents.values().forEach(agent -> {
            boolean alive;
            if (agent instanceof Agent.Movable) {
                alive = tickMovableAgent((Agent.Movable) agent);
            } else {
                alive = agent.tick();
            }
            if (!alive) {
                LOG.debug("{} died", agent);
                cemetery.add(agent);
            }
        });
        cemetery.forEach(this::removeAgent);
    }

    private boolean tickMovableAgent(Agent.Movable agent) {
        Position.Immutable originalPosition = agent.getPosition();
        grid[originalPosition.getY()][originalPosition.getX()] = null;
        boolean alive = agent.tick();
        if (alive) {
            Position.Immutable newPosition = agent.getPosition();
            Agent collidedAgent = grid[newPosition.getY()][newPosition.getX()];
            if (collidedAgent != null) {
                newPosition = resolveCollision(agent, originalPosition, newPosition);
            }
            grid[newPosition.getY()][newPosition.getX()] = agent;
            if (!originalPosition.equals(newPosition)) {
                LOG.debug("Moved {} from {} to {}", agent, originalPosition, newPosition);
            }
        }

        return alive;
    }

    private Position.Immutable resolveCollision(Agent.Movable colliderAgent, Position.Immutable originalPosition, Position.Immutable newPosition) {
        Orientation colliderOrientation = originalPosition.directionTo(newPosition);
        Position adjustedPosition;
        do {
            adjustedPosition = colliderAgent.getMovablePosition().move(colliderOrientation.opposite(), 1);
            LOG.trace("Adjusting collision at {}", newPosition);
        } while (grid[adjustedPosition.getY()][adjustedPosition.getX()] != null);

        return adjustedPosition.toImmutable();
    }

    public void addAgent(Agent.Living agent) {
        Position.Immutable position = agent.getPosition();
        LOG.info("Adding {} to world", agent);
        grid[position.getY()][position.getX()] = agent;
        agents.put(agent.getId(), agent);
    }

    private void removeAgent(Agent agent) {
        Position.Immutable position = agent.getPosition();
        LOG.info("Removing {} from world", agent);
        grid[position.getY()][position.getX()] = null;
        agents.remove(agent.getId());
    }

    public Agent.Living getAgent(int id) {
        return agents.get(id);
    }

    public Position getEmptyPosition() {
        int x;
        int y;
        do {
            x = RandomProvider.nextInt(1, getWidth() - 1);
            y = RandomProvider.nextInt(1, getHeight() - 1);
        } while (grid[y][x] != null);

        return new Position(x, y);
    }

    public Agent getObjectRelativeTo(int id, int xDelta, int yDelta) {
        Position.Immutable center = agents.get(id).getPosition();
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
