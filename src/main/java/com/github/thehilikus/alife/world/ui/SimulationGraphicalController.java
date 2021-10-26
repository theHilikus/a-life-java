package com.github.thehilikus.alife.world.ui;

import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Receives events from the simulation
 */
public class SimulationGraphicalController implements MouseListener {
    private static final Logger LOG = LoggerFactory.getLogger(SimulationGraphicalController.class);
    private final World.GraphicalView worldView;
    private final InfoPanel infoPanel;
    private Agent selectedAgent;

    public SimulationGraphicalController(World.GraphicalView worldView, InfoPanel infoPanel) {
        this.worldView = worldView;
        this.infoPanel = infoPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        selectedAgent = worldView.getAgentInCoordinates(e.getPoint());
        if (selectedAgent != null) {
            LOG.debug("Displaying details of agent {}", selectedAgent);
            refreshSelectedAgentDetails();
        }
    }

    public void refreshSelectedAgentDetails() {
        if (selectedAgent != null) {
            infoPanel.showAgentDetails(selectedAgent.getDetails());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
