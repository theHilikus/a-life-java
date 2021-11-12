package com.github.thehilikus.alife.world;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agents.views.AgentsView;
import com.github.thehilikus.alife.api.*;
import com.github.thehilikus.alife.world.ui.AgentKeyframe;
import com.github.thehilikus.alife.world.ui.InfoPanel;
import com.github.thehilikus.alife.world.ui.Keyframe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.*;
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
        try {
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

            boolean shouldContinue = true;
            if (worldListener != null) {
                shouldContinue = worldListener.ticked(hour);
            }

            boolean movableAgentsAlive = livingAgents.stream().anyMatch(agent -> agent instanceof Agent.Movable);
            if (!movableAgentsAlive) {
                if (worldListener != null) {
                    worldListener.ended(hour);
                }
            }
            LOG.info("Ending hour {}. Will continue? {}\n", hour, movableAgentsAlive && shouldContinue);
            return movableAgentsAlive && shouldContinue;
        } catch (Exception | AssertionError exc) {
            LOG.error("Error simulating the World", exc);
            return false;
        }
    }

    private VitalSign tickMovableAgent(Agent.Movable agent) {
        Position.Immutable originalPosition = agent.getPosition();
        VitalSign causeOfDeath = agent.tick();
        if (causeOfDeath == null) {
            Position.Immutable newPosition = agent.getPosition();
            if (!originalPosition.equals(newPosition)) {
                LOG.debug("Moved {} from {} to {}", agent, originalPosition, newPosition);
            }
            assert newPosition.getX() > 0
                    && newPosition.getY() > 0
                    && newPosition.getX() <= width - 2
                    && newPosition.getY() <= height - 2 : "Agent " + agent.getId() + " moved out of the world: " + newPosition;
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

    private Map<String, Object> getAgentDetails(int agentId) {
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

    public void setWorldListener(WorldListener listener) {
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
            double agentAngleInRadians = Math.atan2(yDelta, xDelta);
            int direction = (int) Math.round(Math.toDegrees(agentAngleInRadians) - centerAgent.getOrientation()) % Locomotion.FULL_TURN;
            if (direction < -Locomotion.HALF_TURN) {
                //represent it in the other direction to make it smaller than 180
                direction = (direction + Locomotion.FULL_TURN) % Locomotion.FULL_TURN;
            }
            assert Math.abs(direction) <= Locomotion.HALF_TURN : "Relative angle must be > -180 and < 180 but was " + direction;
            result.add(new ScanResult(xDelta * xDelta + yDelta * yDelta, direction, found));
        }

        return result;
    }

    public class ConsoleView {

        private final Semaphore semaphore = new Semaphore(0);
        private final Agent.View agentsView = new AgentsView();
        private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        public void runAutomatic(int refreshDelay) {
            executor.scheduleAtFixedRate(semaphore::release, 0, refreshDelay, TimeUnit.MILLISECONDS);
        }

        public void refresh() throws InterruptedException {
            refreshNonBlocking();
            semaphore.drainPermits();
            semaphore.acquire();
        }

        public void refreshNonBlocking() {
            String textualRepresentation = getWorldRepresentation();
            System.out.println(textualRepresentation);
        }

        private String getWorldRepresentation() {
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

        public void printAgentDetails(int agentId) {
            Map<String, Object> agentDetails = getAgentDetails(agentId);
            if (!agentDetails.isEmpty()) {
                StringBuilder detailsBuffer = new StringBuilder();
                detailsBuffer.append("##### Details of agent ").append(agentId).append(" #####").append(System.lineSeparator());
                agentDetails.forEach((key, value) -> detailsBuffer.append(key).append(": ").append(value).append(System.lineSeparator()));
                System.out.println(detailsBuffer);
            } else {
                System.out.println("No agent found with id " + agentId);
            }
        }
    }

    public class GraphicalView extends JPanel implements ActionListener {

        private static final int FRAME_RATE = 30; //FPS
        private final Agent.View agentsView = new AgentsView();

        private final Map<Shape, Integer> agentsShapes = new HashMap<>();
        private int agentSelectedId = -1;
        private final Timer animationClock = new Timer(1000 / FRAME_RATE, this);
        private int currentFrame = 0;
        private int totalFrames;
        private int refreshDelay;
        private final BlockingQueue<Keyframe> frameBuffer = new ArrayBlockingQueue<>(5);
        private Keyframe lastKeyframe;
        private Keyframe nextKeyframe;
        private boolean stopAfterNext;
        private final InfoPanel infoPanel;

        public GraphicalView(InfoPanel infoPanel) {
            this.infoPanel = infoPanel;
            final int edgePadding = 20;
            setPreferredSize(new Dimension(World.this.getWidth() + edgePadding, World.this.getHeight() + edgePadding));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setBackground(Color.WHITE);
            animationClock.setActionCommand("animation-timer");
        }

        public void refresh() throws InterruptedException {
            if (SwingUtilities.isEventDispatchThread()) {
                throw new IllegalStateException("Don't refresh from the EDT");
            }

            Keyframe newFrame = new Keyframe(hour);
            for (Agent agent : livingAgents) {
                newFrame.addAgentFrame(agentsView.createAgentFrame(agent));
            }
            LOG.debug("Frame buffer size = {}/{}. Adding keyframe for hour {}. ", frameBuffer.size(), frameBuffer.size() + frameBuffer.remainingCapacity(), hour);
            frameBuffer.put(newFrame);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!SwingUtilities.isEventDispatchThread()) {
                throw new IllegalStateException("Don't draw outside the EDT");
            }
            super.paintComponent(g);

            Keyframe workingFrame;
            if (animationClock.isRunning() && currentFrame == 1 || lastKeyframe == null) {
                workingFrame = frameBuffer.poll();
            } else {
                workingFrame = lastKeyframe;
            }
            if (workingFrame != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (currentFrame == totalFrames || totalFrames == 0 || currentFrame == 0) {
                    if (nextKeyframe == null) {
                        //for simulation initialization only
                        nextKeyframe = workingFrame;
                    }
                    LOG.trace("Painting keyframe for hour = {}", nextKeyframe.getWorldAge());
                    paintAgentsKeyframes(g2d, nextKeyframe);
                    lastKeyframe = nextKeyframe;
                    currentFrame = 0;
                    if (animationClock.isRunning() && stopAfterNext) {
                        animationClock.stop();
                        stopAfterNext = false;
                    }
                } else {
                    if (currentFrame == 1) {
                        nextKeyframe = workingFrame;
                    }
                    LOG.trace("Painting tween frame # {} between hour {} and {}", currentFrame, lastKeyframe.getWorldAge(), nextKeyframe.getWorldAge());
                    paintAgentsTweenFrames(g2d, lastKeyframe, nextKeyframe, (double) currentFrame / totalFrames);
                }
            } else {
                LOG.warn("No frames available");
                detectIfSimulationNotMoving();
            }
        }

        private void paintAgentsKeyframes(Graphics2D g2d, Iterable<AgentKeyframe> newKeyframe) {
            for (AgentKeyframe agentFrame : newKeyframe) {
                boolean selectedAgent = agentFrame.getAgentId() == agentSelectedId;
                Shape agentShape = agentsView.drawKeyframe(g2d, agentFrame, selectedAgent);
                agentsShapes.put(agentShape, agentFrame.getAgentId());
                if (selectedAgent) {
                    infoPanel.showAgentDetails(agentFrame.getAgentDetails());
                }
            }
        }

        private void paintAgentsTweenFrames(Graphics2D g2d, Keyframe lastKeyframe, Iterable<AgentKeyframe> newKeyframe, double percentageToKeyframe) {
            for (AgentKeyframe agentNewFrame : newKeyframe) {
                AgentKeyframe agentLastFrame = lastKeyframe.getAgentKeyframe(agentNewFrame.getAgentId());
                if (agentLastFrame == null) {
                    //new agent in the world. just take its new keyframe as previous keyframe
                    agentLastFrame = agentNewFrame;
                }
                agentsView.drawTweenFrame(g2d, agentLastFrame, agentNewFrame, percentageToKeyframe);
            }
        }

        private void detectIfSimulationNotMoving() {
            if (currentFrame >= totalFrames + 5) {
                LOG.info("Simulation has not progressed in a while. Stopping animation clock");
                animationClock.stop();
            }
        }


        public void selectAgentIn(Point clickPoint) {
            double shortestDistance = Double.MAX_VALUE;
            int result = -1;
            for (Map.Entry<Shape, Integer> agentShape : agentsShapes.entrySet()) {
                if (agentShape.getKey().getBounds2D().contains(clickPoint)) {
                    Position.Immutable agentPosition = (Position.Immutable) lastKeyframe.getAgentKeyframe(agentShape.getValue()).getAgentDetails().get("position");
                    double distanceToAgent = clickPoint.distanceSq(agentPosition.getX(), agentPosition.getY());
                    if (distanceToAgent < shortestDistance) {
                        shortestDistance = distanceToAgent;
                        result = agentShape.getValue();
                    }
                }
            }
            LOG.debug("Displaying details of agent {}", result);
            agentSelectedId = result;
            repaint();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand().toLowerCase()) {
                case "reset":
                    break;
                case "step":
                    stopAfterNext = true;
                case "start":
                    totalFrames = (int) ((double) refreshDelay / 1000 * FRAME_RATE);
                    LOG.trace("Starting animation clock");
                    animationClock.start();
                    break;
                case "pause":
                    stopAfterNext = true;
                    break;
                case "animation-timer":
                    LOG.trace("Animation clock ticked");
                    currentFrame++;
                    repaint();
                    break;
            }
        }

        public void setRefreshDelay(int refreshDelay) {
            this.refreshDelay = refreshDelay;
        }
    }

    public interface WorldListener {
        boolean ticked(int hour);

        void ended(int hour);
    }
}
