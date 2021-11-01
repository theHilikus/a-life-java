package com.github.thehilikus.alife.world.ui;

import com.github.thehilikus.alife.world.Simulation;
import com.github.thehilikus.alife.world.World;

import java.util.Scanner;

/**
 * Receives events from the simulation
 */
public class SimulationConsoleController implements World.WorldListener {
    private final World.ConsoleView consoleView;
    private final Simulation.Control control;
    private boolean inManualMode = true;

    public SimulationConsoleController(World.ConsoleView consoleView, Simulation.Control control) {
        this.consoleView = consoleView;
        this.control = control;
    }

    @Override
    public boolean ticked(int hour) {
        boolean result = true;
        if (inManualMode) {
            consoleView.refreshNonBlocking();
            showCommandPrompt();
        } else {
            try {
                consoleView.refresh();
            } catch (InterruptedException exc) {
                result = false;
            }
        }

        return result;
    }

    @Override
    public void ended(int hour) {
        System.out.println("Ending simulation after " + hour + " hours");
    }

    public void start(int refreshDelay) {
        if (refreshDelay != 0) {
            inManualMode = false;
            consoleView.runAutomatic(refreshDelay);
            control.start();
        } else {
            showCommandPrompt();
            runManual();
        }
    }

    private void runManual() {
        try (Scanner scanner = new Scanner(System.in)) {
            String command = getCommand(scanner);
            while (!command.equals("q")) {
                if (command.equals("a")) {
                    final int defaultRefreshDelay = 500;
                    start(defaultRefreshDelay);
                    break;
                } else if (command.startsWith("d ")) {
                    String agentId = command.substring(2);
                    queryAgent(agentId);
                    command = getCommand(scanner);
                    continue;
                } else {
                    System.out.println("Unknown command. Ignoring it");
                }

                control.step();
                command = getCommand(scanner);
            }
        }
    }

    private String getCommand(Scanner scanner) {
        String command;
        command = scanner.nextLine();
        return command;
    }

    private void queryAgent(String agentId) {
        if (agentId.chars().allMatch(Character::isDigit)) {
            consoleView.printAgentDetails(Integer.parseInt(agentId));
            showCommandPrompt();
        } else {
            System.err.println("Invalid id \"" + agentId + "\". Ignoring command");
        }
    }


    private void showCommandPrompt() {
        System.out.println("Enter command to run (a = 'continue automatically', d <id> ='details of agent', q = 'quit'");
        System.out.print("> ");
    }
}
