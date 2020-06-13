package com.github.thehilikus.alife.world;

import com.github.thehilikus.alife.agents.FoodAgent;
import com.github.thehilikus.alife.agents.HuntingAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * The driver of the world
 */
public class Simulation {
    private static final Logger LOG = LoggerFactory.getLogger(Simulation.class.getSimpleName());
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final World world;

    private boolean automatic = false;

    public static void main(String[] args) {
        Simulation simulation = new Simulation(args);
        simulation.start();
    }

    private Simulation(String[] argsArray) {

        processArguments(argsArray);

        int worldWidth = 100;
        int worldHeight = 100;
        int foodCount = 50;
        int huntersCount = 20;
        world = new World(worldWidth, worldHeight);

        HuntingAgent.create(huntersCount, world);
//        FoodAgent.create(foodCount, world);
    }

    private void processArguments(String[] argsArray) {
        List<String> args = Arrays.asList(argsArray);
        if (args.contains("--automatic")) {
            automatic = true;
        }
    }

    private void start() {
        if (automatic) {
            runAutomatic();
        } else {
            runManual();
        }
    }

    private void runAutomatic() {
        ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(world::tick, 0, 3, TimeUnit.SECONDS);

        try {
            scheduledFuture.get();
        } catch (InterruptedException | ExecutionException exc) {
            LOG.error("Simulation ended with error", exc);
        }
    }

    private void runManual() {
        String command = "";
        try (Scanner scanner = new Scanner(System.in)) {
            while (!command.equals("x")) {
                if (command.equals("a")) {
                    automatic = true;
                    start();
                    break;
                } else if (command.startsWith("q ")) {
                    String agentId = command.substring(2);
                    queryAgent(agentId);
                }

                world.tick();
                System.out.println("Enter command to run");
                System.out.print("> ");
                command = scanner.nextLine();
            }
            System.out.println("Exiting simulation");
        }
    }

    private void queryAgent(String agentId) {
        if (agentId.chars().allMatch( Character::isDigit )) {
            Map<String, String> agentDetails = world.getAgentDetails(Integer.parseInt(agentId));
            StringBuilder detailsBuffer = new StringBuilder();
            detailsBuffer.append("Details of agent ").append(agentId).append('\n');
            agentDetails.forEach((key, value) -> detailsBuffer.append(key).append(": ").append(value));
            System.out.println(detailsBuffer.toString());
        } else {
            System.err.println("Invalid id \"" + agentId + "\". Ignoring command");
        }
    }
}
