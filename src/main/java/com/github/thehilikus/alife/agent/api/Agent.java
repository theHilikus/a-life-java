package com.github.thehilikus.alife.agent.api;

import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
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

    default int getOrientation() {
        return 0;
    }

    /**
     * Min size for any agent
     */
    int MIN_SIZE = 6;

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
    }

    interface Eatable extends Living {
        int transferEnergy(int desiredBiteSize);
    }

    /**
     * An agent that has a genotype
     */
    interface Evolvable extends Living {
        @NotNull
        Genome getGenome();
    }

    /**
     * An agent that can communicate with others
     */
    interface Social extends Evolvable {
        void communicate(Message message);
    }

}
