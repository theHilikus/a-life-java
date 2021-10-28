package com.github.thehilikus.alife.world.ui;

import com.github.thehilikus.alife.world.World;

import javax.swing.*;
import java.awt.*;

/**
 * The main graphical view of the application
 */
public class SimulationGraphicalView extends JFrame {
    private final MainToolbar toolbar;
    private final InfoPanel infoPanel;

    public SimulationGraphicalView(World.GraphicalView worldView) throws HeadlessException {
        super("Artificial Life");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        add(worldView, BorderLayout.CENTER);

        infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.EAST);

        toolbar = new MainToolbar();
        add(toolbar, BorderLayout.NORTH);

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

    public void addActionListener(SimulationGraphicalController listener) {
        toolbar.addActionListener(listener);
        toolbar.setInitialValue();
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }
}
