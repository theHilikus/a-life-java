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
    @Option(name = "--log-file", metaVar = "logFile")
    private File logFile;

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
}
