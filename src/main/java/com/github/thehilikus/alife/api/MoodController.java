package com.github.thehilikus.alife.api;

/**
 * Controller of transitions between moods
 */
public interface MoodController {
    Mood startHunting(Agent.Living target);

    Mood startScouting();

    Mood startSleeping();

    Mood startEating();

    Mood startIdling();
}
