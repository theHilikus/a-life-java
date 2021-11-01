package com.github.thehilikus.alife.world.ui;

import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.world.Simulation;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Receives events from the simulation
 */
public class SimulationGraphicalController implements MouseListener, World.WorldListener, ActionListener, ChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(SimulationGraphicalController.class.getSimpleName());
    private final World.GraphicalView worldView;
    private final InfoPanel infoPanel;
    private final MainToolbar toolbar;
    private final Simulation.Control control;
    private Agent selectedAgent;

    public SimulationGraphicalController(World.GraphicalView worldView, InfoPanel infoPanel, MainToolbar toolbar, Simulation.Control control) {
        this.worldView = worldView;
        this.infoPanel = infoPanel;
        this.toolbar = toolbar;
        this.control = control;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        selectedAgent = worldView.getAgentInCoordinates(e.getPoint());
        if (selectedAgent != null) {
            LOG.debug("Displaying details of agent {}", selectedAgent);
            refreshSelectedAgentDetails();
            worldView.setSelectedAgent(selectedAgent.getId());
        }
    }

    private void refreshSelectedAgentDetails() {
        if (selectedAgent != null) {
            SwingUtilities.invokeLater(() -> infoPanel.showAgentDetails(selectedAgent.getDetails()));
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

    @Override
    public boolean ticked(int hour) {
        boolean result = true;
        try {
            LOG.trace("World ticked after hour = {}", hour);
            try {
                worldView.refresh(hour);
            } catch (InterruptedException exc) {
                result = false;
            }
            refreshSelectedAgentDetails();
        } catch (Exception exc) {
            LOG.error("Error refreshing the view", exc);
        }

        return result;
    }

    @Override
    public void ended(int hour) {
        toolbar.end();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand().toLowerCase()) {
            case "reset":
                control.reset();
                break;
            case "step":
                control.step();
                break;
            case "start":
                control.start();
                break;
            case "pause":
                control.pause();
                break;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        if (!slider.getValueIsAdjusting()) {
            worldView.setRefreshDelay(slider.getValue());
        }
    }
}
