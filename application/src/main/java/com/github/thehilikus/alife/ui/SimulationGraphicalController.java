package com.github.thehilikus.alife.ui;

import com.github.thehilikus.alife.simulation.view.GraphicalView;
import com.github.thehilikus.alife.ui.swing.MainToolbar;
import com.github.thehilikus.alife.world.WorldListener;
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
public class SimulationGraphicalController implements MouseListener, WorldListener, ActionListener, ChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(SimulationGraphicalController.class);
    private final GraphicalView worldView;
    private final Animation animation;
    private final MainToolbar toolbar;

    public SimulationGraphicalController(GraphicalView worldView, Animation animation, MainToolbar toolbar) {
        this.worldView = worldView;
        this.animation = animation;
        this.toolbar = toolbar;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        worldView.selectAgentIn(e.getPoint());
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
    public boolean ticked(WorldStatus latestStatus) {
        boolean result = true;
        try {
            LOG.trace("World ticked after hour = {}", latestStatus.getAge());
            try {
                worldView.createNextKeyframe(latestStatus);
            } catch (InterruptedException exc) {
                result = false;
            }
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
        animation.actionPerformed(e);
        worldView.actionPerformed(e);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        if (!slider.getValueIsAdjusting()) {
            animation.setRefreshDelay(slider.getValue());
        }
    }
}
