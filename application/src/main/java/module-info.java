module a.life.application {
    requires args4j;
    requires org.slf4j;
    requires java.desktop;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires a.life.core;
    requires l2fprod.properties.editor;
    requires JCDP;

    opens com.github.thehilikus.alife.simulation to args4j;
}