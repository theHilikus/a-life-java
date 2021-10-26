package com.github.thehilikus.alife.world.ui;

import com.l2fprod.common.propertysheet.*;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Shows details about the selected agent
 */
public class AgentDetailsPanel extends PropertySheetPanel {
    public AgentDetailsPanel() {
        setBorder(BorderFactory.createTitledBorder("Agent Details"));

        setMode(PropertySheet.VIEW_AS_CATEGORIES);
        setDescriptionVisible(true);
        setSortingCategories(true);
        setSortingProperties(true);
        setRestoreToggleStates(true);
        setMaximumSize(new Dimension(300, 300));
    }

    public void updateDetails(Map<String, Object> agentDetails) {
        throw new UnsupportedOperationException("Not implemented yet"); //TODO: implement
    }
}
