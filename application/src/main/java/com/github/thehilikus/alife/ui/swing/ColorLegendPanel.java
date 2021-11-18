package com.github.thehilikus.alife.ui.swing;

import com.github.thehilikus.alife.ui.views.HerbivoreView;
import com.github.thehilikus.alife.ui.views.PlantView;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

class ColorLegendPanel extends JPanel {
    public ColorLegendPanel() {
        setBorder(BorderFactory.createTitledBorder("Moods"));
//        setMaximumSize(new Dimension(250, 300));
    }

    public void populate() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        addLegendGroup("Herbivores", HerbivoreView.graphicalMoodColours);
        addLegendGroup("Plants", PlantView.graphicalMoodColours);
    }

    private void addLegendGroup(String groupName, Map<String, Color> graphicalMoodColours) {
        JPanel legendName = new JPanel(new GridLayout(0, 2, 100, 10));
        legendName.setBorder(BorderFactory.createTitledBorder(null, groupName, TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        add(legendName);
        graphicalMoodColours.forEach((key, val) -> addItem(legendName, key, val));
    }

    private void addItem(JPanel panel, String name, Color color) {
        JLabel text = new JLabel(name);
        text.setHorizontalAlignment(JLabel.CENTER);
        panel.add(text);
        JLabel item = new JLabel("");
        item.setBackground(color);
        item.setOpaque(true);
        item.setPreferredSize(new Dimension(50, 20));
        panel.add(item);
    }
}
