package com.github.thehilikus.alife.agents.animals.visions;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.ScanResult;
import com.github.thehilikus.alife.api.Vision;
import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Vision that can see in a square around the agent
 */
public class SurroundingsVision implements Vision {
    private static final Logger LOG = LoggerFactory.getLogger(SurroundingsVision.class.getSimpleName());

    private final int agentId;

    private final int radius;
    private final World world;

    public SurroundingsVision(int agentId, Genome genome, World world) {
        this.agentId = agentId;
        this.radius = genome.getGene(Vision.PARAMETER_PREFIX + "radius");
        this.world = world;
    }

    @Override
    public int getAgentId() {
        return agentId;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.of(PARAMETER_PREFIX + "radius", Integer.toString(radius));
    }

    @Override
    public <T extends Agent> SortedSet<ScanResult> scan(Class<T> type) {
        LOG.debug("Scanning for Agents of type {} around agent {}", type.getSimpleName(), agentId);
        SortedSet<ScanResult> result = new TreeSet<>();
        for (int row = radius * -1; row < radius; row++) {
            for (int col = radius * -1; col < radius; col++) {
                Agent scanResult = world.getObjectRelativeTo(agentId, row, col);
                if (scanResult != null && scanResult.getClass().equals(type)) {
                    ScanResult e = new ScanResult((row << 1) + (col << 1), scanResult);
                    result.add(e);
                }
            }
        }

        LOG.trace("Found {} agents", result.size());
        return result;
    }
}
