package com.github.thehilikus.alife.world;

import com.github.thehilikus.alife.agent.api.LivingAgent;
import com.github.thehilikus.alife.agent.moods.*;
import com.github.thehilikus.alife.agent.moods.api.Mood;
import com.github.thehilikus.alife.agent.plants.Plant;
import com.github.thehilikus.alife.agent.social.Herbivore;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Gathers stats about the world
 */
public class WorldStatistics {
    private final Map<String, List<Integer>> histograms = new HashMap<>();
    private final List<Class<? extends Mood>> moodList;
    private final List<Class<? extends LivingAgent>> typeList;
    private final Collection<String> deathCauses = new HashSet<>();
    private final List<Class<? extends Mood>> plantMoods;

    public WorldStatistics() {
        List<Class<? extends Mood>> herbivoreMoods = Arrays.asList(Eating.class, Existing.class, Hunting.class, InHeat.class, InHeatChasing.class, Mating.class, Scouting.class, Sleeping.class);
        plantMoods = Arrays.asList(BeingEaten.class, Growing.class);
        moodList = Stream.concat(herbivoreMoods.stream(), plantMoods.stream()).collect(Collectors.toList());
        typeList = Arrays.asList(Herbivore.class, Plant.class);
    }

    public void incrementDeathCount(String cause) {
        deathCauses.add(cause);
        incrementSeries(cause, 1);
    }

    public void incrementSeries(String name, int hour) {
        List<Integer> series = histograms.computeIfAbsent(name, x -> new ArrayList<>());
        if (series.size() == hour) {
            series.set(hour - 1, series.get(hour - 1) + 1);
        } else {
            series.add(1);
        }
    }

    public void completeSeries(int hour) {
        for (var mood : moodList) {
            List<Integer> series = histograms.computeIfAbsent(mood.getSimpleName(), x -> new ArrayList<>());
            if (series.size() < hour) {
                series.add(0);
            }
        }

        for (var type : typeList) {
            List<Integer> series = histograms.computeIfAbsent(type.getSimpleName(), x -> new ArrayList<>());
            if (series.size() < hour) {
                series.add(0);
            }
        }
    }

    public void printStats() {
        System.out.println("******** STATISTICS ********");
        printMoodHistogram();
        printTypeHistogram();
        printDeathHistogram();
        System.out.println("******* /STATISTICS/ *******\n");
    }

    private void printMoodHistogram() {
        System.out.println("\t*** Mood series ***");
        for (var mood : moodList) {
            String moodName = mood.getSimpleName();
            List<Integer> series = histograms.get(moodName);
            System.out.println(moodName + series);
        }
        System.out.println("\t*** Mood percentages ***");
        int seriesSize = histograms.get(moodList.get(0).getSimpleName()).size() - 1;
        for (var mood : moodList) {
            String moodName = mood.getSimpleName();
            List<Integer> moodSeries = histograms.get(moodName);
            double accumulated = 0;
            for (int pos = 0; pos < seriesSize; pos++) {
                double totalAgents;
                if (plantMoods.contains(mood)) {
                    totalAgents = histograms.get(Plant.class.getSimpleName()).get(pos);
                } else {
                    totalAgents = histograms.get(Herbivore.class.getSimpleName()).get(pos);
                }
                accumulated += moodSeries.get(pos) / totalAgents;
            }
            System.out.println(moodName + ":" + String.format("%.2f%%", accumulated / seriesSize * 100) );
        }

    }

    private void printTypeHistogram() {
        System.out.println("\t*** Population by type ***");
        for (var type : typeList) {
            String moodName = type.getSimpleName();
            List<Integer> series = histograms.get(moodName);
            System.out.println(moodName + series);
        }
    }

    private void printDeathHistogram() {
        System.out.println("\t*** Death causes by type and cause ***");
        for (var cause : deathCauses) {
            System.out.println(cause + histograms.get(cause));
        }
    }
}
