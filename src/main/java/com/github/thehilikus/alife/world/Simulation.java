package com.github.thehilikus.alife.world;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.spi.AppenderAttachable;
import com.github.thehilikus.alife.agents.plants.Plant;
import com.github.thehilikus.alife.agents.animals.Herbivore;
import com.github.thehilikus.alife.world.ui.SimulationGraphicalController;
import com.github.thehilikus.alife.world.ui.SimulationGraphicalView;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * The driver of the world
 */
public class Simulation {
    private static final Logger LOG = LoggerFactory.getLogger(Simulation.class.getSimpleName());
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final World world;
    private final CliOptions options;
    private static final int FIXED_SEED = 311;
    private final Control control;
    private final World.ConsoleView consoleView;


    public static void main(String[] args) {
        CliOptions options = parseArguments(args);
        if (options != null) {
            if (options.getLogFile() != null) {
                setupLogFile(options);
            }
            Simulation simulation = new Simulation(options);
            simulation.start();
        }
    }

    private static void setupLogFile(CliOptions options) {
        Context loggerContext = (Context) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern("%relative %logger - %msg%n");
        encoder.start();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
        fileAppender.setContext(loggerContext);
        fileAppender.setName("timestamp");
        fileAppender.setFile(options.getLogFile().getAbsolutePath());
        fileAppender.setAppend(false);
        fileAppender.setEncoder(encoder);
        fileAppender.start();

        // attach the rolling file appender to the logger of your choice
        @SuppressWarnings("unchecked")
        AppenderAttachable<ILoggingEvent> logbackLogger = (AppenderAttachable<ILoggingEvent>) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logbackLogger.addAppender(fileAppender);
        logbackLogger.detachAppender("CONSOLE");
    }

    private static CliOptions parseArguments(String[] args) {

        CliOptions result = new CliOptions();
        ParserProperties defaults = ParserProperties.defaults();
        final int usageWidth = 150;
        defaults.withUsageWidth(usageWidth);
        CmdLineParser parser = new CmdLineParser(result, defaults);
        try {
            parser.parseArgument(args);
            return result;
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.err.println();
        }

        return null;
    }

    private Simulation(CliOptions options) {
        this.options = options;

        if (options.isNotRandom()) {
            RandomProvider.setSeed(FIXED_SEED);
        } else {
            RandomProvider.setSeed(new Random().nextLong());
        }

        world = World.createWorld(options);
        Herbivore.create(options.getHerbivoresCount(), world);
        Plant.create(options.getPlantsCount(), world);

        control = new Control(world);

        if (options.isPrintWorld()) {
            consoleView = world.new ConsoleView();
            consoleView.draw();
        } else {
            consoleView = null;
        }
        if (options.isGraphical()) {
            initGui();
        }
    }

    private void initGui() {
        LOG.info("Initializing GUI");

        World.GraphicalView worldView = world.new GraphicalView();
        SimulationGraphicalView simulationView = new SimulationGraphicalView(worldView);
        SimulationGraphicalController graphicalController = new SimulationGraphicalController(worldView, simulationView.getInfoPanel(), simulationView.getToolbar(), control);
        simulationView.addActionListener(graphicalController);
        worldView.addMouseListener(graphicalController);
        world.setTickListener(graphicalController);

        SwingUtilities.invokeLater(() -> simulationView.setVisible(true));
    }

    private void start() {
        if (options.getAutomatic() != 0) {
            runAutomatic(options.getAutomatic());
        } else {
            runManual();
        }
        System.out.println("Ending simulation after " + world.getAge() + " hours");
    }

    private void runAutomatic(int refreshDelay) {
        Runnable tick = () -> {
            boolean alive = world.tick();
            if (options.isPrintWorld()) {
                consoleView.draw();
            }
            if (!alive) {
                executor.shutdown();
            }
        };
        ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(tick, 0, refreshDelay, TimeUnit.MILLISECONDS);

        try {
            scheduledFuture.get();
        } catch (CancellationException exc) {
            //clean exit
        } catch (InterruptedException | ExecutionException exc) {
            LOG.error("Simulation ended with error", exc);
        }
    }

    private void runManual() {
        try (Scanner scanner = new Scanner(System.in)) {
            String command = getCommand(scanner);
            while (!command.equals("q")) {
                if (command.equals("a")) {
                    final int defaultRefresh = 500;
                    options.setAutomatic(defaultRefresh);
                    start();
                    break;
                } else if (command.startsWith("d ")) {
                    String agentId = command.substring(2);
                    queryAgent(agentId);
                    command = getCommand(scanner);
                    continue;
                } else {
                    System.out.println("Unknown command. Ignoring it");
                }

                boolean alive = world.tick();
                if (!alive) {
                    break;
                }
                if (options.isPrintWorld()) {
                    consoleView.draw();
                }
                command = getCommand(scanner);
            }
        }
    }

    private String getCommand(Scanner scanner) {
        String command;
        System.out.println("Enter command to run (a = 'continue automatically', d <id> ='details of agent', q = 'quit'");
        System.out.print("> ");
        command = scanner.nextLine();
        return command;
    }

    private void queryAgent(String agentId) {
        if (agentId.chars().allMatch(Character::isDigit)) {
            Map<String, Object> agentDetails = world.getAgentDetails(Integer.parseInt(agentId));
            if (!agentDetails.isEmpty()) {
                StringBuilder detailsBuffer = new StringBuilder();
                detailsBuffer.append("##### Details of agent ").append(agentId).append(" #####").append(System.lineSeparator());
                agentDetails.forEach((key, value) -> detailsBuffer.append(key).append(": ").append(value).append(System.lineSeparator()));
                System.out.println(detailsBuffer);
            } else {
                System.out.println("No agent found with id " + agentId);
            }
        } else {
            System.err.println("Invalid id \"" + agentId + "\". Ignoring command");
        }
    }

    public static class Control {
        private static final Logger LOG = LoggerFactory.getLogger(Control.class);

        private final World world;
        private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        private Future<?> future;
        private int refreshDelay;

        public Control(World world) {
            this.world = world;
        }

        public void start() {
            LOG.info("Starting continuous simulation with refresh delay = {}", refreshDelay);
            Runnable tick = () -> {
                boolean alive = true;
                while (alive) {
                    alive = world.tick();
                }
                pause();
            };
            future = executor.submit(tick);
        }

        public void pause() {
            LOG.info("Pausing simulation");
            future.cancel(true);
            future = null;
        }

        public void reset() {
            LOG.info("Resetting simulation");
            if (isRunning()) {
                pause();
            }
            throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
        }

        public void tick() {
            LOG.info("Advancing a single tick");
            executor.execute(world::tick);
        }

        public boolean isRunning() {
            return future != null;
        }

        public void setRefreshDelay(int refreshDelay) {
            if (this.refreshDelay != refreshDelay) {
                LOG.info("Changing refresh delay from {}ms to {}ms", this.refreshDelay, refreshDelay);
                this.refreshDelay = refreshDelay;
            }
        }
    }
}
