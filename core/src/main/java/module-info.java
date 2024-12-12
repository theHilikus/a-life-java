module a.life.core {
    requires org.slf4j;
    requires java.validation;
    exports com.github.thehilikus.alife.world to a.life.application;
    exports com.github.thehilikus.alife.agent.api;
    exports com.github.thehilikus.alife.agent.motion.api;
    exports com.github.thehilikus.alife.agent.vision.api;
    exports com.github.thehilikus.alife.agent.vitals.api;
    exports com.github.thehilikus.alife.agent.moods.api;
    exports com.github.thehilikus.alife.agent.factories;
    exports com.github.thehilikus.alife.simulator;
}