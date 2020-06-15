package com.github.thehilikus.alife.world;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.spi.AppenderAttachable;
import com.github.thehilikus.alife.agents.plants.Plant;
import com.github.thehilikus.alife.agents.animals.Herbivore;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final CliOptions options;

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
        AppenderAttachable<ILoggingEvent> logbackLogger = (AppenderAttachable<ILoggingEvent>) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logbackLogger.addAppender(fileAppender);
        logbackLogger.detachAppender("console");
    }

    private static CliOptions parseArguments(String[] args) {

        CliOptions result = new CliOptions();
        ParserProperties defaults = ParserProperties.defaults();
        defaults.withUsageWidth(150);
        CmdLineParser parser = new CmdLineParser(result,defaults);
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
        int worldWidth = 100;
        int worldHeight = 100;
        int foodCount = 50;
        int huntersCount = 20;
        world = new World(worldWidth, worldHeight);

        Herbivore.create(huntersCount, world);
        Plant.create(foodCount, world);

        if (options.isPrintWorld()) {
            System.out.println(world.getRepresentation());
        }
    }

    private void start() {
        if (options.isAutomatic()) {
            runAutomatic();
        } else {
            runManual();
        }
    }

    private void runAutomatic() {
        Runnable tick = () -> {
            world.tick();
            if (options.isPrintWorld()) {
                System.out.println(world.getRepresentation());
            }
        };
        ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(tick, 0, 3, TimeUnit.SECONDS);

        try {
            scheduledFuture.get();
        } catch (InterruptedException | ExecutionException exc) {
            LOG.error("Simulation ended with error", exc);
        }
    }

    private void runManual() {
        try (Scanner scanner = new Scanner(System.in)) {
            String command = getCommand(scanner);
            while (!command.equals("q")) {
                if (command.equals("a")) {
                    options.setAutomatic(true);
                    start();
                    break;
                } else if (command.startsWith("d ")) {
                    String agentId = command.substring(2);
                    queryAgent(agentId);
                }

                world.tick();
                if (options.isPrintWorld()) {
                    System.out.println(world.getRepresentation());
                }
                command = getCommand(scanner);
            }
            System.out.println("Exiting simulation");
        }
    }

    private String getCommand(Scanner scanner) {
        String command;
        System.out.println("Enter command to run");
        System.out.print("> ");
        command = scanner.nextLine();
        return command;
    }

    private void queryAgent(String agentId) {
        if (agentId.chars().allMatch( Character::isDigit )) {
            Map<String, String> agentDetails = world.getAgentDetails(Integer.parseInt(agentId));
            StringBuilder detailsBuffer = new StringBuilder();
            detailsBuffer.append("##### Details of agent ").append(agentId).append(" #####").append(System.lineSeparator());
            agentDetails.forEach((key, value) -> detailsBuffer.append(key).append(": ").append(value).append(System.lineSeparator()));
            System.out.println(detailsBuffer.toString());
        } else {
            System.err.println("Invalid id \"" + agentId + "\". Ignoring command");
        }
    }
}
