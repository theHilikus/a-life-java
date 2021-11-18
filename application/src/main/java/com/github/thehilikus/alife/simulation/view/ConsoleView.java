package com.github.thehilikus.alife.simulation.view;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agent.api.Agent;
import com.github.thehilikus.alife.ui.views.AgentView;
import com.github.thehilikus.alife.ui.views.AgentsViewDelegator;
import com.github.thehilikus.alife.world.World;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Prints a representation of the simulation in STDOUT
 */
public class ConsoleView {

    private final World world;
    private final Semaphore semaphore = new Semaphore(0);
    private final AgentView agentsView = new AgentsViewDelegator();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public ConsoleView(World world) {
        this.world = world;
    }

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
        StringBuilder stringBuilder = new StringBuilder(world.getWidth() * world.getHeight() * 2);
        stringBuilder.append("World view on hour ").append(world.getAge()).append(System.lineSeparator());

        String formatCode = Ansi.generateCode(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE);
        String emptySpace = Ansi.formatMessage("  ", formatCode);
        Agent[][] grid = new Agent[world.getHeight()][world.getWidth()];
        world.getLivingAgents().forEach(agent -> grid[agent.getPosition().getY()][agent.getPosition().getX()] = agent);
        world.getEdges().forEach(agent -> grid[agent.getPosition().getY()][agent.getPosition().getX()] = agent);

        for (int y = 0; y < world.getHeight(); y++) {
            for (int x = 0; x < world.getWidth(); x++) {
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
        Map<String, Object> agentDetails = world.getAgentDetails(agentId);
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
