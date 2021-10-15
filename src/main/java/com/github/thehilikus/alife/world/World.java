package com.github.thehilikus.alife.world;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The environment where the agents live
 */
public class World {
    private static final Logger LOG = LoggerFactory.getLogger(World.class.getSimpleName());
    /**
     * Globally accessible reference to the world
     */
    public static World instance;
    private final Agent[][] grid;
    private final Map<Integer, Agent.Living> agents = new ConcurrentHashMap<>();
    private final Map<Integer, Agent.Living> cemetery = new HashMap<>();
    private int hour;


    static World createWorld(CliOptions options) {
        instance = new World(options.getWorldWidth(), options.getWorldHeight());
        return instance;
    }

    public World(int width, int height) {
        grid = new Agent[height + 2][width + 2];
        createEdge();
        hour = 0;
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
        return grid[0].length;
    }

    private int getHeight() {
        return grid.length;
    }

    public boolean tick() {
        LOG.info("Starting hour {}", ++hour);
        Collection<Agent.Living> toRemove = new HashSet<>();
        agents.values().forEach(agent -> {
            VitalSign causeOfDeath;
            if (agent instanceof Agent.Movable) {
                causeOfDeath = tickMovableAgent((Agent.Movable) agent);
            } else {
                causeOfDeath = agent.tick();
            }
            if (causeOfDeath != null) {
                LOG.debug("{} died. Cause of death={}", agent, causeOfDeath);
                toRemove.add(agent);
            }
        });
        toRemove.forEach(this::removeAgent);
        LOG.info("Ending hour {}\n", hour);

        return agents.values().stream().anyMatch(agent -> agent instanceof Agent.Movable);
    }

    private VitalSign tickMovableAgent(Agent.Movable agent) {
        Position.Immutable originalPosition = agent.getPosition();
        grid[originalPosition.getY()][originalPosition.getX()] = null;
        VitalSign causeOfDeath = agent.tick();
        if (causeOfDeath == null) {
            Position.Immutable newPosition = agent.getPosition();
            Agent collidedAgent = grid[newPosition.getY()][newPosition.getX()];
            if (collidedAgent != null) {
                newPosition = resolveCollision(agent, originalPosition, collidedAgent);
            }
            grid[newPosition.getY()][newPosition.getX()] = agent;
            if (!originalPosition.equals(newPosition)) {
                LOG.debug("Moved {} from {} to {}", agent, originalPosition, newPosition);
            }
        }

        return causeOfDeath;
    }

    private Position.Immutable resolveCollision(Agent.Living colliderAgent, Position.Immutable originalPosition, Agent collidedAgent) {
        Position newPosition = new Position(originalPosition.getX(), originalPosition.getY());
        Orientation newOrientation = originalPosition.directionTo(colliderAgent.getPosition()).opposite(); //TODO maybe needs to be random to avoid oscillation
        colliderAgent.changePosition(newPosition, newOrientation);
        LOG.trace("Adjusting collision at {} between new {} and original {}", newPosition, colliderAgent.getId(), collidedAgent.getId());
        collidedAgent = grid[newPosition.getY()][newPosition.getX()];
        if (collidedAgent != null) {
            throw new UnsupportedOperationException("When resolving collision by sending agent back, the old position is already taken"); //TODO: improve instead of throwing
        }

        return newPosition.toImmutable();
    }

    public void addAgent(Agent.Living agent) {
        Position.Immutable position = agent.getPosition();
        LOG.info("Adding {} to world", agent);
        grid[position.getY()][position.getX()] = agent;
        agents.put(agent.getId(), agent);
    }

    private void removeAgent(Agent.Living agent) {
        Position.Immutable position = agent.getPosition();
        LOG.info("Removing {} from world", agent);
        grid[position.getY()][position.getX()] = null;
        agents.remove(agent.getId());
        cemetery.put(agent.getId(), agent);
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
        Map<String, String> result;
        Agent agent = agents.get(agentId);
        if (agent == null) {
            agent = cemetery.get(agentId);
            if (agent == null) {
                return Collections.emptyMap();
            }
        }
        result = agent.getDetails();

        return result;
    }

    public String getRepresentation() {
        StringBuilder stringBuilder = new StringBuilder(getWidth() * getHeight() * 2);
        stringBuilder.append("World view on hour ").append(hour).append(System.lineSeparator());

        String formatCode = Ansi.generateCode(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE);
        String emptySpace = Ansi.formatMessage("  ", formatCode);
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
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

    public int getAge() {
        return hour;
    }
}
