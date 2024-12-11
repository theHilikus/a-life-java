package com.github.thehilikus.alife.simulation.view;

import com.diogonunes.jcdp.color.api.Ansi;
import com.github.thehilikus.alife.agent.api.AgentDetails;
import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.ui.views.AgentView;
import com.github.thehilikus.alife.ui.views.AgentsViewDelegator;
import com.github.thehilikus.alife.world.WorldListener;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Prints a representation of the simulation in STDOUT
 */
public class ConsoleView {

    private final boolean printWorld;
    private WorldListener.WorldStatus latestStatus;
    private final Semaphore semaphore = new Semaphore(0);
    private final AgentView agentsView = new AgentsViewDelegator();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public ConsoleView(boolean printWorld) {
        this.printWorld = printWorld;
    }

    public void runAutomatic(int refreshDelay) {
        executor.scheduleAtFixedRate(semaphore::release, 0, refreshDelay, TimeUnit.MILLISECONDS);
    }

    public void refresh(WorldListener.WorldStatus latestStatus) throws InterruptedException {
        refreshNonBlocking(latestStatus);
        semaphore.drainPermits();
        semaphore.acquire();
    }

    public void refreshNonBlocking(WorldListener.WorldStatus latestStatus) {
        this.latestStatus = latestStatus;
        if (printWorld) {
            String textualRepresentation = createWorldRepresentation(latestStatus);
            System.out.println(textualRepresentation);
        }
    }

    private String createWorldRepresentation(WorldListener.WorldStatus latestStatus) {
        StringBuilder stringBuilder = new StringBuilder(latestStatus.getWidth() * latestStatus.getHeight() * 2);
        stringBuilder.append("World view on hour ").append(latestStatus.getAge()).append(System.lineSeparator());

        String formatCode = Ansi.generateCode(Ansi.Attribute.NONE, Ansi.FColor.NONE, Ansi.BColor.NONE);
        String emptySpace = Ansi.formatMessage("  ", formatCode);
        AgentDetails.Immutable[][] grid = new AgentDetails.Immutable[latestStatus.getHeight()][latestStatus.getWidth()];
        latestStatus.getLivingAgentsDetails().forEach(agentDetails -> {
            Position.Immutable agentPosition = agentDetails.getPosition();
            grid[agentPosition.getY()][agentPosition.getX()] = agentDetails;
        });
        latestStatus.getEdges().forEach(agentDetails -> {
            Position.Immutable agentPosition = agentDetails.getPosition();
            grid[agentPosition.getY()][agentPosition.getX()] = agentDetails;
        });

        for (int y = 0; y < latestStatus.getHeight(); y++) {
            for (int x = 0; x < latestStatus.getWidth(); x++) {
                AgentDetails.Immutable agentDetails = grid[y][x];
                if (agentDetails == null) {
                    stringBuilder.append(emptySpace);
                } else {
                    agentsView.drawInConsole(stringBuilder, agentDetails);
                }
            }
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    public void printAgentDetails(int agentId) {
        Optional<AgentDetails.Immutable> agentDetailsOptional = latestStatus.getAgentDetails(agentId);
        if (agentDetailsOptional.isPresent()) {
            StringBuilder detailsBuffer = new StringBuilder();
            detailsBuffer.append("##### Details of agent ").append(agentId).append(" #####").append(System.lineSeparator());
            AgentDetails.Immutable immutable = agentDetailsOptional.get();
            immutable.forEach((key, value) -> detailsBuffer.append(key).append(": ").append(value).append(System.lineSeparator()));
            System.out.println(detailsBuffer);
        } else {
            System.out.println("No agent found with id " + agentId);
        }
    }
}
