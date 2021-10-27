package com.github.thehilikus.alife.world.ui;

import com.github.thehilikus.alife.world.World;
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
    private final SimulationGraphicalController controller;

    public SimulationGraphicalView(World.GraphicalView worldView) throws HeadlessException {
        super("Artificial Life");
        this.worldView = worldView;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        add(worldView, BorderLayout.CENTER);
        InfoPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.LINE_END);
        controller = new SimulationGraphicalController(worldView, infoPanel);
        worldView.addMouseListener(controller);
        pack();
        setLocationRelativeTo(null);
    }

    private InfoPanel createInfoPanel() {
        InfoPanel result = new InfoPanel();
        LayoutManager layout = new BoxLayout(result, BoxLayout.PAGE_AXIS);
        result.setLayout(layout);
        result.populate();

        return result;
    }

    public void refresh() {
        try {
            worldView.refresh();
            controller.refreshSelectedAgentDetails();
        } catch (Exception exc) {
            LOG.error("Error refreshing the view", exc);
        }
    }
}
