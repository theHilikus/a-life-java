package com.github.thehilikus.alife.world.ui;

import javax.swing.*;

/**
 * Displays information about the simulation
 */
public class InfoPanel extends JPanel {
    public void populate() {
        ColorLegendPanel legendPanel = new ColorLegendPanel();
        add(legendPanel);
        legendPanel.populate();
    }
}
