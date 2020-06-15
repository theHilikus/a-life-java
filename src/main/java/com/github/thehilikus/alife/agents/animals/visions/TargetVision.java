package com.github.thehilikus.alife.agents.animals.visions;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.api.Agent;
import com.github.thehilikus.alife.api.ScanResult;
import com.github.thehilikus.alife.api.Vision;
import com.github.thehilikus.alife.world.World;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A vision with a single target
 */
public class TargetVision implements Vision {
    private final Agent target;
    private final SurroundingsVision surroundingsVision;

    public TargetVision(int agentId, Genome genome, World world, Agent target) {
        surroundingsVision = new SurroundingsVision(agentId, genome, world);
        this.target = target;
    }

    @Override
    public <T extends Agent> SortedSet<ScanResult> scan(Class<T> type) {
        SortedSet<ScanResult> scanResults = surroundingsVision.scan(type);
        SortedSet<ScanResult> result = new TreeSet<>();
        for (ScanResult scanResult : scanResults) {
            if (scanResult.getAgent().getId() == target.getId()) {
                result.add(scanResult);
            }
        }

        return result;
    }

    @Override
    public int getAgentId() {
        return surroundingsVision.getAgentId();
    }

    @Override
    public Map<String, String> getParameters() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(Vision.PARAMETER_PREFIX + "targetId", Integer.toString(target.getId()));
        result.putAll(surroundingsVision.getParameters());

        return result;
    }
}
