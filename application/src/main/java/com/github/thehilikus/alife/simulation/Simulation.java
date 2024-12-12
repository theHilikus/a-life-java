package com.github.thehilikus.alife.simulation;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.spi.AppenderAttachable;
import com.github.thehilikus.alife.agent.api.LivingAgentFactory;
import com.github.thehilikus.alife.agent.api.RandomProvider;
import com.github.thehilikus.alife.agent.factories.HerbivoreFactory;
import com.github.thehilikus.alife.agent.factories.PlantFactory;
import com.github.thehilikus.alife.simulation.view.ConsoleView;
import com.github.thehilikus.alife.simulation.view.GraphicalView;
import com.github.thehilikus.alife.simulator.Control;
import com.github.thehilikus.alife.ui.Animation;
import com.github.thehilikus.alife.ui.SimulationConsoleController;
import com.github.thehilikus.alife.ui.SimulationGraphicalController;
import com.github.thehilikus.alife.ui.SimulationGraphicalView;
import com.github.thehilikus.alife.ui.swing.InfoPanel;
import com.github.thehilikus.alife.world.World;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Random;

/**
 * The driver of the world
 */
public class Simulation {
    private static final Logger LOG = LoggerFactory.getLogger(Simulation.class);
    private final World world;
    private final CliOptions options;
    private static final int FIXED_SEED = 311;
    private final Control control;
    private SimulationConsoleController consoleController;
    private SimulationGraphicalView simulationView;

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
        FileAppender<ILoggingEvent> fileAppender = createLogFileAppender(options);
        fileAppender.start();

        // attach the rolling file appender to the logger of your choice
        @SuppressWarnings("unchecked")
        AppenderAttachable<ILoggingEvent> logbackLogger = (AppenderAttachable<ILoggingEvent>) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logbackLogger.addAppender(fileAppender);
        logbackLogger.detachAppender("CONSOLE");
    }

    private static FileAppender<ILoggingEvent> createLogFileAppender(CliOptions options) {
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
        return fileAppender;
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

        world = new World(options.getWorldWidth(), options.getWorldHeight());
        LivingAgentFactory.setWorld(world);
        new HerbivoreFactory().createAgents(options.getHerbivoresCount());
        new PlantFactory().createAgents(options.getPlantsCount());
        control = new Control(world);

        if (options.isGraphical()) {
            initGui();
            control.start();
        } else {
            initConsole(options.isPrintWorld());
        }
    }

    private void initConsole(boolean printWorld) {
        ConsoleView consoleView = new ConsoleView(printWorld);
        consoleController = new SimulationConsoleController(consoleView, control);
        world.setWorldListener(consoleController);
        consoleView.refreshNonBlocking(world);
    }

    private void initGui() {
        LOG.info("Initializing GUI");

        InfoPanel infoPanel = new InfoPanel();
        Animation animation = new Animation();
        GraphicalView worldView = new GraphicalView(world, infoPanel, animation);
        simulationView = new SimulationGraphicalView(worldView, infoPanel);
        SimulationGraphicalController graphicalController = new SimulationGraphicalController(worldView, animation, simulationView.getToolbar());
        simulationView.addActionListener(graphicalController);
        worldView.addMouseListener(graphicalController);
        animation.addActionListener(graphicalController);
        world.setWorldListener(graphicalController);
        try {
            worldView.createNextKeyframe(world);
        } catch (InterruptedException e) {
            throw new AssertionError("Should never happen since there are no interrupts at this stage");
        }
    }

    private void start() {
        if (options.isGraphical()) {
            SwingUtilities.invokeLater(() -> simulationView.setVisible(true));
        } else {
            consoleController.start(options.getAutomatic());
        }
    }

}
