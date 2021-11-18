package com.github.thehilikus.alife.ui.swing;

import com.github.thehilikus.alife.ui.SimulationGraphicalController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Toolbar with controls for the animation
 */
public class MainToolbar extends JToolBar {
    private static final int INITIAL_REFRESH_DELAY = 500;

    private final JButton restartButton;
    private final JButton stepButton;
    private final JButton startAndPauseButton;
    private final JSlider speedSelector;

    public MainToolbar() {
        super("Controls", HORIZONTAL);
        setRollover(true);
        setBorderPainted(true);
        setLayout(new FlowLayout(FlowLayout.CENTER));

        restartButton = new JButton("Reset");
        restartButton.setActionCommand("reset");
        restartButton.setVisible(false); //TODO: remove once restart is implemented
        add(restartButton);

        addSeparator();

        stepButton = new JButton("Step");
        stepButton.setActionCommand("step");
        add(stepButton);

        startAndPauseButton = new JButton("Start");
        add(startAndPauseButton);

        speedSelector = new JSlider(100, 1000);
        speedSelector.setMajorTickSpacing(100);
        speedSelector.setPaintTicks(false);
        speedSelector.setPaintLabels(true);
        speedSelector.setSnapToTicks(true);
        speedSelector.setOpaque(false);
        speedSelector.setInverted(true);
        Dictionary<Integer, JComponent> labels = new Hashtable<>();
        labels.put(1000, new JLabel("Slowest"));
        labels.put(100, new JLabel("Fastest"));
        speedSelector.setLabelTable(labels);
        add(speedSelector);
    }

    public void addActionListener(SimulationGraphicalController listener) {
        restartButton.addActionListener(listener);
        stepButton.addActionListener(listener);
        startAndPauseButton.addActionListener(listener);
        startAndPauseButton.addActionListener(this::startPauseClicked);
        speedSelector.addChangeListener(listener);
    }

    private void startPauseClicked(ActionEvent e) {
        boolean isCurrentlyStart = e.getActionCommand().equalsIgnoreCase("start");
        if (isCurrentlyStart) {
            startAndPauseButton.setText("Pause");
        } else {
            startAndPauseButton.setText("Start");
        }
        stepButton.setEnabled(!isCurrentlyStart);
        speedSelector.setEnabled(!isCurrentlyStart);
    }

    public void setInitialValue() {
        speedSelector.setValue(INITIAL_REFRESH_DELAY);
    }

    public void end() {
        startAndPauseButton.setText("Start");
        startAndPauseButton.setEnabled(false);
        stepButton.setEnabled(false);
        speedSelector.setEnabled(false);
    }
}
