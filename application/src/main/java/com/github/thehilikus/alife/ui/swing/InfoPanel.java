package com.github.thehilikus.alife.ui.swing;

import com.github.thehilikus.alife.agent.api.AgentDetails;

import javax.swing.*;

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

    public void showAgentDetails(AgentDetails.Immutable details) {
        agentDetailsPanel.updateDetails(details);
    }
}
