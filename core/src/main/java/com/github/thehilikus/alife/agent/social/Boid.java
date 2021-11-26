package com.github.thehilikus.alife.agent.social;

import com.github.thehilikus.alife.agent.controllers.SocialController;
import com.github.thehilikus.alife.agent.controllers.VitalsController;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.vitals.AgentModules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Boid extends BasicSocialAgent {
    private static final Logger LOG = LoggerFactory.getLogger(Boid.class.getSimpleName());

    public Boid(int id, AgentModules dependencies, Mood startingMood, VitalsController vitals, SocialController social) {
        super(id, dependencies, startingMood, vitals, social);
    }

    @Override
    protected Logger getLog() {
        return LOG;
    }

    @Override
    public String toString() {
        return "Boid{" +
                "id=" + getId() +
                ", position=" + getPosition() +
                ", mood=" + getMood() +
                '}';
    }
}
