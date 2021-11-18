package com.github.thehilikus.alife.simulation.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Helper to run animations
 */
public class Animation implements ActionListener {
    private static final Logger LOG = LoggerFactory.getLogger(Animation.class.getSimpleName());
    private static final int FRAME_RATE = 30; //FPS
    private final Timer animationClock = new Timer(1000 / FRAME_RATE, null);
    private int currentFrame = 0;
    private int totalFrames;
    private int refreshDelay;
    private boolean stopAfterNext;

    public Animation() {
        animationClock.setActionCommand("animation-timer");
    }

    public boolean isKeyframe() {
        return currentFrame == totalFrames || currentFrame == 0;
    }

    public void keyframeCompleted() {
        currentFrame = 0;
        if (animationClock.isRunning() && stopAfterNext) {
            animationClock.stop();
            stopAfterNext = false;
        }
    }

    public double getPercentageComplete() {
        return (double) currentFrame / totalFrames;
    }

    public boolean isFirstTween() {
        return currentFrame == 1;
    }

    public Object getCurrentFrame() {
        return currentFrame;
    }

    public void setRefreshDelay(int refreshDelay) {
        this.refreshDelay = refreshDelay;
    }

    public void addActionListener(ActionListener listener) {
        animationClock.addActionListener(listener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand().toLowerCase()) {
            case "reset":
                break;
            case "step":
                stopAfterNext = true;
                //noinspection fallthrough
            case "start":
                totalFrames = (int) ((double) refreshDelay / 1000 * FRAME_RATE);
                LOG.trace("Starting animation clock");
                animationClock.start();
                break;
            case "pause":
                stopAfterNext = true;
                break;
            case "animation-timer":
                LOG.trace("Animation clock ticked");
                currentFrame++;
                break;
        }
    }
}
