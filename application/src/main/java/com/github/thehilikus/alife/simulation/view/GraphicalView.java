package com.github.thehilikus.alife.simulation.view;

import com.github.thehilikus.alife.agent.api.Position;
import com.github.thehilikus.alife.ui.AgentKeyframe;
import com.github.thehilikus.alife.ui.Animation;
import com.github.thehilikus.alife.ui.Keyframe;
import com.github.thehilikus.alife.ui.swing.InfoPanel;
import com.github.thehilikus.alife.ui.views.AgentView;
import com.github.thehilikus.alife.ui.views.AgentsViewDelegator;
import com.github.thehilikus.alife.world.WorldListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Prints a representation of the simulation in a Swing JPanel
 */
public class GraphicalView extends JPanel implements ActionListener {
    private static final Logger LOG = LoggerFactory.getLogger(GraphicalView.class);
    private static final int edgePadding = 20;

    private WorldListener.WorldStatus latestStatus;
    private final AgentView agentsView = new AgentsViewDelegator();
    private final Map<Shape, Integer> agentsShapes = new HashMap<>();
    private int agentSelectedId = -1;
    private final Animation animation;
    private final BlockingQueue<Keyframe> frameBuffer = new ArrayBlockingQueue<>(5);
    private Keyframe lastKeyframe;
    private Keyframe nextKeyframe;

    private final InfoPanel infoPanel;

    public GraphicalView(WorldListener.WorldStatus latestStatus, InfoPanel infoPanel, Animation animation) {
        this.infoPanel = infoPanel;
        this.animation = animation;
        setPreferredSize(new Dimension(latestStatus.getWidth() + edgePadding, latestStatus.getHeight() + edgePadding));
        setBackground(Color.WHITE);
    }

    public void createNextKeyframe(WorldListener.WorldStatus latestStatus) throws InterruptedException {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Don't refresh from the EDT");
        }

        this.latestStatus = latestStatus;

        Keyframe newFrame = new Keyframe(latestStatus.getAge());
        for (Map<String, Object> agentDetails : latestStatus.getLivingAgentsDetails()) {
            newFrame.addAgentFrame(agentsView.createAgentFrame(agentDetails));
        }
        LOG.debug("Frame buffer size = {}/{}. Adding keyframe for hour {}. ", frameBuffer.size(), frameBuffer.size() + frameBuffer.remainingCapacity(), latestStatus.getAge());
        frameBuffer.put(newFrame);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Don't draw outside the EDT");
        }
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(edgePadding / 2, edgePadding / 2);

        Shape edge = new Rectangle(latestStatus.getWidth(), latestStatus.getHeight());
        g2d.draw(edge);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (lastKeyframe == null || nextKeyframe == null) {
            initialDraw(g2d);
            return;
        }

        if (animation.isKeyframe()) {
            paintAgentsKeyframes(g2d, nextKeyframe);
            lastKeyframe = nextKeyframe;
        } else {
            paintAgentsTweenFrames(g2d, lastKeyframe, nextKeyframe, animation.getPercentageComplete());
        }
    }

    private void initialDraw(Graphics2D g2d) {
        if (lastKeyframe == null) {
            lastKeyframe = frameBuffer.poll();
        } else {
            nextKeyframe = lastKeyframe;
        }
        paintAgentsKeyframes(g2d, lastKeyframe);
    }

    private void paintAgentsKeyframes(Graphics2D g2d, Iterable<AgentKeyframe> newKeyframe) {
        if (nextKeyframe == null) {
            LOG.trace("Painting initial keyframe");
        } else {
            LOG.trace("Painting keyframe for hour = {}", nextKeyframe.getWorldAge());
        }

        agentsShapes.clear();
        for (AgentKeyframe agentFrame : newKeyframe) {
            boolean selectedAgent = agentFrame.getAgentId() == agentSelectedId;
            Shape agentShape = agentsView.drawKeyframe(g2d, agentFrame, selectedAgent);
            agentsShapes.put(agentShape, agentFrame.getAgentId());
            if (selectedAgent) {
                infoPanel.showAgentDetails(agentFrame.getAgentDetails());
            }
        }
        animation.keyframeCompleted();
    }

    private void paintAgentsTweenFrames(Graphics2D g2d, Keyframe lastKeyframe, Iterable<AgentKeyframe> newKeyframe, double percentageToKeyframe) {
        if (animation.isFirstTween()) {
            Keyframe bufferedFrame = frameBuffer.poll();
            if (bufferedFrame == null) {
                LOG.warn("No frames available. Pausing animation");
                animation.actionPerformed(new ActionEvent(this, 1, "pause"));
                return;
            } else {
                nextKeyframe = bufferedFrame;
            }
        }

        LOG.trace("Painting tween frame # {} between hour {} and {}", animation.getCurrentFrame(), lastKeyframe.getWorldAge(), nextKeyframe.getWorldAge());
        for (AgentKeyframe agentNewFrame : newKeyframe) {
            AgentKeyframe agentLastFrame = lastKeyframe.getAgentKeyframe(agentNewFrame.getAgentId());
            if (agentLastFrame == null) {
                //new agent in the world. just take its new keyframe as previous keyframe
                agentLastFrame = agentNewFrame;
            }
            agentsView.drawTweenFrame(g2d, agentLastFrame, agentNewFrame, percentageToKeyframe);
        }
    }

    public void selectAgentIn(Point clickPoint) {
        double shortestDistance = Double.MAX_VALUE;
        clickPoint.translate(-edgePadding / 2, -edgePadding / 2);
        int result = -1;
        for (Map.Entry<Shape, Integer> agentShape : agentsShapes.entrySet()) {
            if (agentShape.getKey().getBounds2D().contains(clickPoint)) {
                AgentKeyframe agentKeyframe = lastKeyframe.getAgentKeyframe(agentShape.getValue());
                Position.Immutable agentPosition = agentKeyframe.getAgentDetail("position");
                double distanceToAgent = clickPoint.distanceSq(agentPosition.getX(), agentPosition.getY());
                if (distanceToAgent < shortestDistance) {
                    shortestDistance = distanceToAgent;
                    result = agentShape.getValue();
                }
            }
        }
        LOG.debug("Displaying details of agent {}", result);
        agentSelectedId = result;
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("animation-timer".equalsIgnoreCase(e.getActionCommand())) {
            repaint();
        }
    }
}
