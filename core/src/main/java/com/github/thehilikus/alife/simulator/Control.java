package com.github.thehilikus.alife.simulator;

import com.github.thehilikus.alife.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Interface to control the simulation
 */
public class Control {
    private static final Logger LOG = LoggerFactory.getLogger(Control.class);

    private final World world;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "com/github/thehilikus/alife/simulation"));
    private Future<?> future;

    public Control(World world) {
        this.world = world;
    }

    public void start() {
        LOG.info("Starting continuous simulation");
        Runnable tick = () -> {
            boolean alive = true;
            while (alive) {
                alive = world.tick();
            }
            pause();
        };
        future = executor.submit(tick);
    }

    public void pause() {
        LOG.info("Pausing simulation");
        future.cancel(true);
        future = null;
    }

    public void reset() {
        LOG.info("Resetting simulation");
        if (isRunning()) {
            pause();
        }
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }

    public void step() {
        LOG.info("Advancing a single step");
        executor.execute(world::tick);
    }

    public boolean isRunning() {
        return future != null;
    }
}
