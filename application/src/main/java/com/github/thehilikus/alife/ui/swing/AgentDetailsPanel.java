package com.github.thehilikus.alife.ui.swing;

import com.github.thehilikus.alife.agent.api.AgentDetails;
import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;

import javax.swing.*;
import java.util.Map;

/**
 * Shows details about the selected agent
 */
public class AgentDetailsPanel extends PropertySheetPanel {
    public AgentDetailsPanel() {
        setBorder(BorderFactory.createTitledBorder("Agent Details"));

        setMode(VIEW_AS_CATEGORIES);
        setSortingCategories(true);
        setSortingProperties(true);
        setRestoreToggleStates(true);
    }

    public void updateDetails(AgentDetails.Immutable agentDetails) {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Don't draw outside the EDT");
        }
        PropertySheetTableModel tableModel = new PropertySheetTableModel();

        for (Map.Entry<String, Object> detailsEntry : agentDetails.entrySet()) {
            String[] detailSplit = detailsEntry.getKey().split("\\.");
            String category = detailSplit.length > 1 ? detailSplit[0] : "basic";
            String name = detailSplit.length > 1 ? detailSplit[1] : detailSplit[0];

            DefaultProperty property = new DefaultProperty();
            property.setCategory(category.substring(0, 1).toUpperCase() + category.substring(1));
            property.setDisplayName(name);
            property.setName(name);

            Object value = detailsEntry.getValue();
            if (value instanceof Double) {
                value = String.format("%.3f", value);
            }
            property.setValue(value);

            tableModel.addProperty(property);
        }


        tableModel.setMode(((PropertySheetTableModel)getTable().getModel()).getMode());
        tableModel.setSortingCategories(isSortingCategories());
        tableModel.setSortingProperties(isSortingProperties());
        PropertySheetTable table = new PropertySheetTable(tableModel);

        setTable(table);
    }
}
