package com.github.thehilikus.alife.world;

import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.api.internal.SocialAgent;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.vision.api.ScanResult;
import com.github.thehilikus.alife.agent.vision.api.Shape;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The environment where the agents live
 */
public class World {
    private static final Logger LOG = LoggerFactory.getLogger(World.class);
    private final Collection<LivingAgent> livingAgents = new CopyOnWriteArrayList<>();
    private final Collection<Agent> edges = new ArrayList<>();
    private final Map<Integer, LivingAgent> cemetery = new HashMap<>();
    private final int width;
    private final int height;
    private int hour;
    private WorldListener worldListener;
    private final WorldStats stats = new WorldStats();

    public World(int width, int height) {
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
            Collection<LivingAgent> toRemove = new HashSet<>();
            livingAgents.forEach(agent -> {
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

                    stats.incrementSeries((String) agent.getDetails().get(Mood.PARAMETER_PREFIX + "current"), hour);
                    stats.incrementSeries(agent.getClass().getSimpleName(), hour);
                } else {
                    LOG.debug("{} died. Cause of death={}", agent, causeOfDeath);
                    stats.incrementDeathCount(agent.getClass().getSimpleName() + ":" + causeOfDeath.getClass().getSimpleName());
                    toRemove.add(agent);
                }
            });
            toRemove.forEach(this::removeAgent);
            stats.completeSeries(hour);

            boolean shouldContinue = true;
            if (worldListener != null) {
                shouldContinue = worldListener.ticked(hour);
            }

            boolean socialAgentsAlive = livingAgents.stream().anyMatch(agent -> agent instanceof SocialAgent);
            if (!socialAgentsAlive) {
                if (worldListener != null) {
                    worldListener.ended(hour);
                }
            }
            boolean result = socialAgentsAlive && shouldContinue;
            LOG.info("Ending hour {}. Will continue? {}\n", hour, result);
            if (!result) {
                stats.printStats();
            }

            return result;
        } catch (Exception | AssertionError exc) {
            LOG.error("Error simulating the World", exc);
            return false;
        }
    }

    public void addAgent(Agent agent) {
        LOG.info("Adding {} to world", agent);
        livingAgents.add((LivingAgent) agent);
    }

    private void removeAgent(LivingAgent agent) {
        LOG.info("Removing {} from world", agent);
        livingAgents.remove(agent);
        cemetery.put(agent.getId(), agent);
    }

    private Optional<LivingAgent> getLivingAgent(int agentId) {
        return livingAgents.stream().filter(agent2 -> agent2.getId() == agentId).findFirst();
    }

    public Position getRandomPosition() {
        int x = RandomProvider.nextInt(1, getWidth() - 1);
        int y = RandomProvider.nextInt(1, getHeight() - 1);
        return new Position(x, y);
    }

    public Map<String, Object> getAgentDetails(int agentId) {
        Map<String, Object> result;
        Optional<LivingAgent> agentOptional = getLivingAgent(agentId).or(() -> Optional.ofNullable(cemetery.get(agentId)));
        result = agentOptional.map(Agent::getDetails).orElse(Collections.emptyMap());

        return result;
    }

    public int getAge() {
        return hour;
    }

    public void setWorldListener(WorldListener listener) {
        worldListener = listener;
    }

    public SortedSet<ScanResult> getAgentsInAreaRelativeTo(int agentId, Shape viewingArea, Predicate<Agent> test) {
        LivingAgent centerAgent = livingAgents.stream().filter(agent -> agent.getId() == agentId).findFirst().orElseThrow();
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
            int direction = (int) Math.round(Math.toDegrees(agentAngleInRadians) - centerAgent.getOrientation()) % Locomotion.Turn.FULL;
            if (direction < -Locomotion.Turn.HALF) {
                //represent it in the other direction to make it smaller than 180
                direction = (direction + Locomotion.Turn.FULL) % Locomotion.Turn.FULL;
            }
            assert Math.abs(direction) <= Locomotion.Turn.HALF : "Relative angle must be > -180 and < 180 but was " + direction;
            result.add(new ScanResult(xDelta * xDelta + yDelta * yDelta, direction, found));
        }

        return result;
    }

    public Collection<? extends Agent> getLivingAgents() {
        return livingAgents;
    }

    public Collection<Agent> getEdges() {
        return edges;
    }

    public interface WorldListener {
        boolean ticked(int hour);

        void ended(int hour);
    }
}
