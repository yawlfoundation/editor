/*
 * Copyright (c) 2004-2013 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */

package org.yawlfoundation.yawl.editor.ui.properties.data;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @author Michael Adams
 * @date 8/08/12
 */
public class VariableRowUsageEditor extends AbstractCellEditor
        implements TableCellEditor, ActionListener {

    private final VariableTablePanel tablePanel;
    private final JComboBox usageCombo;


    public VariableRowUsageEditor(VariableTablePanel panel) {
        tablePanel = panel;
        usageCombo = new JComboBox(new Vector<VariableScope>(panel.getScopes()));

        usageCombo.setRenderer(new ListCellRenderer() {
            protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            public Component getListCellRendererComponent(JList jList, Object o,
                                                          int i, boolean b, boolean b1) {
                JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(
                        jList, o, i, b, b1);
                label.setText(((VariableScope) o).getLabel());
                return label;
            }
        });

        usageCombo.addActionListener(this);
    }


    public Object getCellEditorValue() {
        return usageCombo.getSelectedItem();
    }


    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row,
                                                 int column) {
        tablePanel.setEditMode(true);
        usageCombo.setSelectedItem(value);
        return usageCombo;
    }


    public boolean stopCellEditing() {
        tablePanel.setEditMode(false);
        return super.stopCellEditing();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        stopCellEditing();
        tablePanel.notifyUsageChange(((VariableScope) getCellEditorValue()).getValue());
    }

}
