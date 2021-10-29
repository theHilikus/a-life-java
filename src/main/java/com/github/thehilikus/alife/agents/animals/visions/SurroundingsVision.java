package com.github.thehilikus.alife.agents.animals.visions;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.ScanResult;
import com.github.thehilikus.alife.api.Vision;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Positive;
import java.awt.*;
import java.util.Map;
import java.util.SortedSet;
import java.util.function.Predicate;

/**
 * Vision that can see in a square around the agent
 */
public class SurroundingsVision implements Vision {
    private static final Logger LOG = LoggerFactory.getLogger(SurroundingsVision.class.getSimpleName());

    private final int agentId;

    @Positive
    private final int distance;
    private final World world;

    public SurroundingsVision(int agentId, Genome genome, World world) {
        this.agentId = agentId;
        this.distance = genome.getGene(Vision.PARAMETER_PREFIX + "distance");
        this.world = world;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, Object> getParameters() {
        return Map.of(PARAMETER_PREFIX + "distance", distance);
    }

    @Override
    public SortedSet<ScanResult> scan(Predicate<Agent> test) {
        LOG.debug("Scanning around agent {} with distance = {}", agentId, distance);
        Shape viewingArea = new Rectangle(-distance / 2, -distance / 2, distance, distance);
        SortedSet<ScanResult> result = world.getAgentsInAreaRelativeTo(agentId, viewingArea, test);

        LOG.trace("Found {} agents", result.size());
        return result;
    }
}
