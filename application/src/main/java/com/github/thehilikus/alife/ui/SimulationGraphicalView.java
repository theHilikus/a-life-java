package com.github.thehilikus.alife.ui;

import com.github.thehilikus.alife.simulation.view.GraphicalView;
import com.github.thehilikus.alife.ui.swing.InfoPanel;
import com.github.thehilikus.alife.ui.swing.MainToolbar;

import javax.swing.*;
import java.awt.*;

/**
 * The main graphical view of the application
 */
public class SimulationGraphicalView extends JFrame {
    private final MainToolbar toolbar;

    public SimulationGraphicalView(GraphicalView worldView, InfoPanel infoPanel) throws HeadlessException {
        super("Artificial Life");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        add(worldView, BorderLayout.CENTER);

        LayoutManager layout = new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS);
        infoPanel.setLayout(layout);
        infoPanel.populate();
        add(infoPanel, BorderLayout.EAST);

        toolbar = new MainToolbar();
        add(toolbar, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    public void addActionListener(SimulationGraphicalController listener) {
        toolbar.addActionListener(listener);
        toolbar.setInitialValue();
    }

    public MainToolbar getToolbar() {
        return toolbar;
    }
}
