package com.github.thehilikus.alife.world;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * The main graphical view of the application
 */
public class SimulationGraphicalView extends JFrame {
    private final World.GraphicalView worldView;
    private static final Logger LOG = LoggerFactory.getLogger(SimulationGraphicalView.class);

    public SimulationGraphicalView(World.GraphicalView worldView) throws HeadlessException {
        super("Artificial Life");
        this.worldView = worldView;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        add(worldView, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    public void refresh() {
        try {
            worldView.refresh();
        } catch (Exception exc) {
            LOG.error("Error refreshing the view", exc);
        }

    }
}
