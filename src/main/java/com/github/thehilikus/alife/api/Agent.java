package com.github.thehilikus.alife.api;

import com.github.thehilikus.alife.agents.genetics.Genome;
import com.github.thehilikus.alife.world.World;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.awt.*;
import java.util.Map;

/**
 * An entity in the World
 */
public interface Agent {
    @PositiveOrZero
    int getId();

    @NotNull
    Map<String, Object> getDetails();

    Position.Immutable getPosition();

    /**
     * Max size for any agent
     */
    int MAX_SIZE = 100;

    /**
     * An agent that does actions in every turn
     */
    interface Living extends Agent {
        /**
         * Execute one turn of the agent
         *
         * @return null if the agent is alive at the end of the turn or the vital sign that caused the death
         */
        VitalSign tick();

        void changePosition(Position newPosition, Orientation direction);
    }

    interface Eatable extends Living {
        int transferEnergy(int desiredBiteSize);
    }

    /**
     * An agent that can change positions
     */
    interface Movable extends Living {

    }

    /**
     * An agent that has a genotype
     */
    interface Evolvable extends Living {
        /**
         * The prefix for evolution parameters
         */
        String PARAMETER_PREFIX = "evolution.";

        @NotNull
        Genome getGenome();

        Evolvable reproduce(int fatherId, World world, Genome offspringGenome);
    }

    interface View {
        void drawInConsole(StringBuilder builder, Agent agent);

        Shape drawIn2DGraphics(Graphics2D g2d, Agent agent, boolean selected);
    }
}
