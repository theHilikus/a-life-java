package com.github.thehilikus.alife.world;

import org.kohsuke.args4j.Option;

import java.io.File;

/**
 * A container of command line options
 */
public class CliOptions {
    @Option(name = "--automatic", usage = "Advance each age of the world automatically without user input")
    private boolean automatic = false;
    @Option(name = "--print-world", usage = "Show a representation of the world in STDOUT")
    private boolean printWorld = true;
    @Option(name = "--log-file", metaVar = "<logFile>", usage = "File to log to. Disables logging to the console")
    private File logFile;
    @Option(name = "--not-random", usage = "Set a fixed seed to the RNG so that the runs are consistent")
    private boolean notRandom;

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
}
