package com.github.thehilikus.alife.api;

import javax.inject.Scope;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Objects in the context of an agent
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AgentScope {
}
