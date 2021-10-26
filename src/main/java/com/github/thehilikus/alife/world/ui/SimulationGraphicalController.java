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

    public SimulationGraphicalController(World.GraphicalView worldView, InfoPanel infoPanel) {
        this.worldView = worldView;
        this.infoPanel = infoPanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Agent agentClicked = worldView.getAgentInCoordinates(e.getPoint());
        if (agentClicked != null) {
            LOG.debug("Displaying details of agent {}", agentClicked);
            infoPanel.showAgentDetails(agentClicked.getDetails());
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
