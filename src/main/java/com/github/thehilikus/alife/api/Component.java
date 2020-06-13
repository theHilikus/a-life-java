package com.github.thehilikus.alife.api;

import java.util.Map;

/**
 * A part of an agent
 */
public interface Component {
    int getAgentId();

    Map<String, String> getParameters();
}
