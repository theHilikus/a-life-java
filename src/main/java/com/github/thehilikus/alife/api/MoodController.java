package com.github.thehilikus.alife.api;

/**
 * Controller of transitions between moods
 */
public interface MoodController {
    Mood startHunting(Agent.Eatable target);

    Mood startScouting();

    Mood startSleeping();

    Mood startEating(Agent.Eatable food);

    Mood startIdling();

    Mood startFollowing(Agent.Evolvable mate);

    Mood startMating(Agent.Evolvable mate);
}
