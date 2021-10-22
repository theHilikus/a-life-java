package com.github.thehilikus.alife.world;

import org.kohsuke.args4j.Option;

import java.io.File;

/**
 * A container of command line options
 */
@SuppressWarnings({"FieldMayBeFinal", "MagicNumber"})
public class CliOptions {
    @Option(name = "--automatic", usage = "Advance each age of the world automatically without user input")
    private boolean automatic = false;
    @Option(name = "--print-world", usage = "Show a representation of the world in STDOUT")
    private boolean printWorld = false;
    @Option(name = "--log-file", metaVar = "<logFile>", usage = "File to log to. Disables logging to the console")
    private File logFile;
    @Option(name = "--not-random", usage = "Set a fixed seed to the RNG so that the runs are consistent")
    private boolean notRandom;
    @Option(name = "--plants", usage = "The number of plants to add to the world")
    private int plantsCount = 50;
    @Option(name = "--herbivores", usage = "The number of herbivores to add to the world")
    private int herbivoresCount = 20;
    @Option(name = "--world-width", usage = "The number of horizontal positions")
    private int worldWidth = 100;
    @Option(name = "--world-height", usage = "The number of vertical positions")
    private int worldHeight = 100;

    public boolean isAutomatic() {
        return automatic;
    }

    public boolean isPrintWorld() {
        return printWorld;
    }

    public File getLogFile() {
        return logFile;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public boolean isNotRandom() {
        return notRandom;
    }

    public int getPlantsCount() {
        return plantsCount;
    }

    public int getHerbivoresCount() {
        return herbivoresCount;
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }
}
