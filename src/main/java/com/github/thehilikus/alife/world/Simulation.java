package com.github.thehilikus.alife.world;

import com.github.thehilikus.alife.agents.FoodAgent;
import com.github.thehilikus.alife.agents.HuntingAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * The driver of the world
 */
public class Simulation {
    private static final Logger LOG = LoggerFactory.getLogger(Simulation.class.getSimpleName());
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final World world;

    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        simulation.start();
    }

    private Simulation() {
        int worldWidth = 100;
        int worldHeight = 100;
        int foodCount = 50;
        int huntersCount = 20;
        world = new World(worldWidth, worldHeight);

        HuntingAgent.create(huntersCount, world);
//        FoodAgent.create(foodCount, world);
    }

    private void start() {
        ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(world::tick, 0, 3, TimeUnit.SECONDS);
        try {
            scheduledFuture.get();
        } catch (InterruptedException | ExecutionException exc) {
            LOG.error("Simulation ended with error", exc);
        }
    }
}
