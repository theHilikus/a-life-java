package com.github.thehilikus.alife.world.ui;

import javax.swing.*;
import java.util.Map;

/**
 * Displays information about the simulation
 */
public class InfoPanel extends JPanel {

    private AgentDetailsPanel agentDetailsPanel;

    public void populate() {
        ColorLegendPanel legendPanel = new ColorLegendPanel();
        add(legendPanel);
        legendPanel.populate();

        agentDetailsPanel = new AgentDetailsPanel();
        add(agentDetailsPanel);
    }

    public void showAgentDetails(Map<String, Object> details) {
        agentDetailsPanel.updateDetails(details);
    }
}
