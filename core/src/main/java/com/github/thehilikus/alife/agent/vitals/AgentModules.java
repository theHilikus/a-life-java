package com.github.thehilikus.alife.agent.vitals;

import com.github.thehilikus.alife.agent.api.internal.Component;
import com.github.thehilikus.alife.agent.genetics.Genome;
import com.github.thehilikus.alife.agent.motion.api.Locomotion;
import com.github.thehilikus.alife.agent.vision.api.Vision;
import com.github.thehilikus.alife.agent.vitals.api.VitalSign;

import java.util.HashMap;
import java.util.Map;

/**
 * A container of objects needed to construct an agent
 */
public class AgentModules {
    private final Genome genome;
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    private final Map<Class<? extends VitalSign>, VitalSign> vitalSigns = new HashMap<>();


    public AgentModules(Genome genome) {
        this.genome = genome;
    }

    public void addComponent(Class<? extends Component> key, Component component) {
        components.put(key, component);
    }

    public void addVitalSign(Class<? extends VitalSign> key, VitalSign vitalSign) {
        vitalSigns.put(key, vitalSign);
    }

    public Vision getVision() {
        return (Vision) components.get(Vision.class);
    }

    public Locomotion getLocomotion() {
        return (Locomotion) components.get(Locomotion.class);
    }

    public Genome getGenome() {
        return genome;
    }

    public HungerTracker getHungerTracker() {
        return (HungerTracker) vitalSigns.get(HungerTracker.class);
    }

    public EnergyTracker getEnergyTracker() {
        return (EnergyTracker) vitalSigns.get(EnergyTracker.class);
    }

    public AgeTracker getAgeTracker() {
        return (AgeTracker) vitalSigns.get(AgeTracker.class);
    }

    public ReproductionTracker getReproductionTracker() {
        return (ReproductionTracker) vitalSigns.get(ReproductionTracker.class);
    }

    public SizeTracker getSizeTracker() {
        return (SizeTracker) vitalSigns.get(SizeTracker.class);
    }
}
