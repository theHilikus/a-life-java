package com.github.thehilikus.alife.world;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.plants.Plant;
import com.github.thehilikus.alife.agents.views.AgentsView;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.Position;
import com.github.thehilikus.alife.api.ScanResult;
import com.github.thehilikus.alife.api.VitalSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The environment where the agents live
 */
public class World {
    private static final Logger LOG = LoggerFactory.getLogger(World.class.getSimpleName());
    private final Collection<Agent.Living> livingAgents = new CopyOnWriteArrayList<>();
    private final Collection<Agent> edges = new ArrayList<>();
    private final Map<Integer, Agent.Living> cemetery = new HashMap<>();
    private final int width;
    private final int height;
    private int hour;
    private WorldListener worldListener;


    static World createWorld(CliOptions options) {
        return new World(options.getWorldWidth(), options.getWorldHeight());
    }

    private World(int width, int height) {
        LOG.info("Creating world of {}x{}", width, height);
        this.width = width + 2;
        this.height = height + 2;
        createEdge();
        hour = 0;
    }

    private void createEdge() {
        int x = 0;
        while (x < getWidth()) {
            Position.Immutable position = new Position(x, 0).toImmutable();
            edges.add(new Edge(position));

            position = new Position(x, getHeight() - 1).toImmutable();
            edges.add(new Edge(position));
            x++;
        }

        int y = 0;
        while (y < getHeight()) {
            Position.Immutable position = new Position(0, y).toImmutable();
            edges.add(new Edge(position));

            position = new Position(getWidth() - 1, y).toImmutable();
            edges.add(new Edge(position));
            y++;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean tick() {
        LOG.info("Starting hour {}", ++hour);
        Collection<Agent.Living> toRemove = new HashSet<>();
        livingAgents.forEach(agent -> {
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
        if (worldListener != null) {
            worldListener.ticked(hour);
        }

        boolean result = livingAgents.stream().anyMatch(agent -> agent instanceof Agent.Movable);
        if (!result) {
            if (worldListener != null) {
                worldListener.ended();
            }
        }
        return result;
    }

    private VitalSign tickMovableAgent(Agent.Movable agent) {
        Position.Immutable originalPosition = agent.getPosition();
        VitalSign causeOfDeath = agent.tick();
        if (causeOfDeath == null) {
            Position.Immutable newPosition = agent.getPosition();
            if (!originalPosition.equals(newPosition)) {
                LOG.debug("Moved {} from {} to {}", agent, originalPosition, newPosition);
            }
        }

        return causeOfDeath;
    }

    public void addAgent(Agent.Living agent) {
        LOG.info("Adding {} to world", agent);
        livingAgents.add(agent);
    }

    private void removeAgent(Agent.Living agent) {
        LOG.info("Removing {} from world", agent);
        livingAgents.remove(agent);
        cemetery.put(agent.getId(), agent);
    }

    public Position getRandomPosition() {
        int x = RandomProvider.nextInt(1, getWidth() - 1);
        int y = RandomProvider.nextInt(1, getHeight() - 1);
        return new Position(x, y);
    }

    public Map<String, Object> getAgentDetails(int agentId) {
        Map<String, Object> result;
        Agent agent = livingAgents.stream().filter(agent2 -> agent2.getId() == agentId).findFirst().orElse(null);
        if (agent == null) {
            agent = cemetery.get(agentId);
            if (agent == null) {
                return Collections.emptyMap();
            }
        }
        result = agent.getDetails();

        return result;
    }

    public int getAge() {
        return hour;
    }

    public void setTickListener(WorldListener listener) {
        worldListener = listener;
    }

    public SortedSet<ScanResult> getAgentsInAreaRelativeTo(int agentId, Shape viewingArea, Predicate<Agent> test) {
        Agent centerAgent = livingAgents.stream().filter(agent -> agent.getId() == agentId).findFirst().orElseThrow();
        int centerAgentX = centerAgent.getPosition().getX();
        int centerAgentY = centerAgent.getPosition().getY();
        Set<Agent> agentsFound = Stream.concat(livingAgents.stream(), edges.stream())
                .filter(test) //test the requirements passed by the agent
                .filter(agent -> viewingArea.contains(agent.getPosition().getX() - centerAgentX, agent.getPosition().getY() - centerAgentY))
                .filter(agent -> agent.getId() != agentId) //to avoid itself
                .collect(Collectors.toSet());

        SortedSet<ScanResult> result = new TreeSet<>();
        for (Agent found : agentsFound) {
            int xDelta = found.getPosition().getX() - centerAgent.getPosition().getX();
            int yDelta = found.getPosition().getY() - centerAgent.getPosition().getY();
            result.add(new ScanResult(xDelta, yDelta, found));
        }

        return result;
    }

    public class ConsoleView {

        private final Agent.View agentsView = new AgentsView();

        public void draw() {
            String textualRepresentation = getWorldRepresentation();
            System.out.println(textualRepresentation);
        }

        public String getWorldRepresentation() {
            StringBuilder stringBuilder = new StringBuilder(getWidth() * getHeight() * 2);
            stringBuilder.append("World view on hour ").append(hour).append(System.lineSeparator());

            String formatCode = Ansi.generateCode(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE);
            String emptySpace = Ansi.formatMessage("  ", formatCode);
            Agent[][] grid = new Agent[getHeight()][getWidth()];
            livingAgents.forEach(agent -> grid[agent.getPosition().getY()][agent.getPosition().getX()] = agent);
            edges.forEach(agent -> grid[agent.getPosition().getY()][agent.getPosition().getX()] = agent);

            for (int y = 0; y < getHeight(); y++) {
                for (int x = 0; x < getWidth(); x++) {
                    Agent agent = grid[y][x];
                    if (agent == null) {
                        stringBuilder.append(emptySpace);
                    } else {
                        agentsView.drawInConsole(stringBuilder, agent);
                    }
                }
                stringBuilder.append(System.lineSeparator());
            }
            return stringBuilder.toString();
        }
    }

    public class GraphicalView extends JPanel implements ActionListener {

        private static final int FRAME_RATE = 30; //FPS
        private final Agent.View agentsView = new AgentsView();

        private final Map<Shape, Agent> agentsShapes = new HashMap<>();
        private int agentSelectedId = -1;
        private Timer animationClock;
        private int currentFrame = 0;
        private int totalFrames;
        private int refreshDelay;

        public GraphicalView() {
            final int edgePadding = 20;
            setPreferredSize(new Dimension(World.this.getWidth() + edgePadding, World.this.getHeight() + edgePadding));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setBackground(Color.WHITE);
        }

        public void refresh(int hour) {
            if (currentFrame != totalFrames) {
                LOG.warn("Dropped {} frames", totalFrames - currentFrame);
            }
            currentFrame = 0;
            totalFrames = (int) ((double) refreshDelay / 1000 * FRAME_RATE);
            if (animationClock == null) {
                animationClock = new Timer(1000 / FRAME_RATE, this);
                LOG.trace("Starting animation clock for hour = {}", hour);
                animationClock.start();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!SwingUtilities.isEventDispatchThread()) {
                throw new IllegalStateException("Don't draw outside the EDT");
            }
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (currentFrame == totalFrames) {
                LOG.trace("Painting keyframe = {}", currentFrame);
                agentsShapes.clear();
                paintAgentsKeyframes(g2d, true); //paint plants first for proper z-ordering
                paintAgentsKeyframes(g2d, false);
            } else {
                LOG.trace("Painting tween frame = {}", currentFrame);
                currentFrame++;
                paintAgentsTweenFrames(g2d, true, (double) currentFrame / totalFrames);
                paintAgentsTweenFrames(g2d, false, (double) currentFrame / totalFrames);
            }
        }

        private void paintAgentsKeyframes(Graphics2D g2d, boolean plantsOnly) {
            for (Agent agent : livingAgents) {
                if (plantsOnly && agent instanceof Plant) {
                    Shape agentShape = agentsView.drawKeyframe(g2d, agent, agent.getId() == agentSelectedId);
                    agentsShapes.put(agentShape, agent);
                }
                if (!plantsOnly && !(agent instanceof Plant)) {
                    Shape agentShape = agentsView.drawKeyframe(g2d, agent, agent.getId() == agentSelectedId);
                    agentsShapes.put(agentShape, agent);
                }
            }
        }

        private void paintAgentsTweenFrames(Graphics2D g2d, boolean plantsOnly, double percentToKeyFrame) {
            for (Agent agent : livingAgents) {
                if (plantsOnly && agent instanceof Plant) {
                    agentsView.drawTweenFrame(g2d, agent, percentToKeyFrame);
                }
                if (!plantsOnly && !(agent instanceof Plant)) {
                    agentsView.drawTweenFrame(g2d, agent, percentToKeyFrame);
                }
            }
        }

        public Agent getAgentInCoordinates(Point point) {
            Agent result = null;
            double shortestDistance = Double.MAX_VALUE;
            for (Map.Entry<Shape, Agent> agentShape : agentsShapes.entrySet()) {
                if (agentShape.getKey().getBounds2D().contains(point)) {
                    Position.Immutable agentPosition = agentShape.getValue().getPosition();
                    double distanceToAgent = point.distanceSq(agentPosition.getX(), agentPosition.getY());
                    if (distanceToAgent < shortestDistance) {
                        shortestDistance = distanceToAgent;
                        result = agentShape.getValue();
                    }
                }
            }

            return result;
        }

        public void setSelectedAgent(int selectedId) {
            agentSelectedId = selectedId;
            repaint();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOG.trace("Animation clock ticked");
            repaint();
        }

        public void setRefreshDelay(int refreshDelay) {
            this.refreshDelay = refreshDelay;
        }

        public void end() {
            animationClock.stop();
        }
    }

    public interface WorldListener {
        void ticked(int hour);

        void ended();
    }
}
