package com.github.thehilikus.alife.world;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The environment where the agents live
 */
public class World {
    private static final Logger LOG = LoggerFactory.getLogger(World.class.getSimpleName());
    private final Agent[][] grid;
    private final Map<Integer, Agent.Living> agents = new ConcurrentHashMap<>();
    private final Map<Integer, Agent.Living> cemetery = new HashMap<>();
    private int hour;


    static World createWorld(CliOptions options) {
        return new World(options.getWorldWidth(), options.getWorldHeight());
    }

    private World(int width, int height) {
        LOG.info("Creating world of {}x{}", width, height);
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

    public int getWidth() {
        return grid[0].length;
    }

    public int getHeight() {
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
        Orientation newOrientation = originalPosition.directionTo(colliderAgent.getPosition()).opposite();
        while (!isPositionEmpty(newPosition.getX(), newPosition.getY())) {
            newPosition.move(newOrientation, 1);
            if (grid[newPosition.getY()][newPosition.getX()] instanceof Edge) {
                LOG.debug("Hit the edge when resolving collision");
                newOrientation = newOrientation.turn(Orientation.WEST);
            }
        }
        colliderAgent.changePosition(newPosition, newOrientation);
        LOG.trace("Adjusting collision at {} between new {} and original {}", newPosition, colliderAgent.getId(), collidedAgent.getId());

        return newPosition.toImmutable();
    }

    public void addAgent(Agent.Living agent) {
        Position.Immutable position = agent.getPosition();
        if (!isPositionEmpty(position.getX(), position.getY())) {
            Position closebyPosition = new Position(position.getX(), position.getY());
            closebyPosition.move(Orientation.WEST, 1);
            resolveCollision(agent, closebyPosition.toImmutable(), grid[position.getY()][position.getX()]);
        }
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
        } while (!isPositionEmpty(x, y));

        return new Position(x, y);
    }

    private boolean isPositionEmpty(int x, int y) {
        return grid[y][x] == null;
    }

    public Agent getAgentRelativeTo(int id, int xDelta, int yDelta) {
        Position.Immutable center = agents.get(id).getPosition();
        int x = center.getX() + xDelta;
        int y = center.getY() + yDelta;
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return null;
        }

        return grid[y][x];
    }

    public Map<String, Object> getAgentDetails(int agentId) {
        Map<String, Object> result;
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

    public class WorldView extends JPanel {
        public WorldView(int width, int height) {
            setPreferredSize(new Dimension(width, height));
            setBackground(Color.WHITE);
        }

        public void refresh() {
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            AgentsView agentsView = new AgentsView();
            for (int y = 1; y < World.this.getHeight() - 1; y++) { //don't draw edges
                for (int x = 1; x < World.this.getWidth() - 1; x++) {
                    Agent agent = grid[y][x];
                    if (agent != null) {
                        agentsView.draw(g2d, agent);
                    }
                }
            }
        }

    }
}
