/*
 * Copyright (c) 2004-2014 The YAWL Foundation. All rights reserved.
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

package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.properties.data.VariableRow;
import org.yawlfoundation.yawl.editor.ui.swing.JSingleSelectTable;
import org.yawlfoundation.yawl.util.XNode;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class DataContextTable extends JSingleSelectTable {

    private XNode _cornerstoneNode;             // for read-only rendering only
    private DialogMode _mode;

    public DataContextTable(DialogMode mode) {
        super();
        _mode = mode;
        setModel(new DataContextTableModel());
        setRowHeight(getRowHeight() + 5);
        disableMoveOnEnter();
        setFillsViewportHeight(true);            // to allow drops on empty table
    }


    public List<VariableRow> getVariables() { return getTableModel().getVariables(); }


    public void setVariables(List<VariableRow> variables) {
        getTableModel().setVariables(variables);
        setPreferredScrollableViewportSize(getPreferredSize());
        updateUI();
    }


    public void setCornerstoneNode(XNode node) {
        _cornerstoneNode = node;
    }


    public VariableRow getSelectedVariable() {
        int selectedRow = getSelectedRow();
        return selectedRow > -1 ? getTableModel().getVariableAtRow(selectedRow) : null;
    }


    public DataContextTableModel getTableModel() {
        return (DataContextTableModel) getModel();
    }


    public boolean hasValidContent() {
        for (VariableRow row : getVariables()) {
            if (! row.isValidValue()) return false;
        }
        return true;
    }


    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
        JComponent component = (JComponent) super.prepareRenderer(renderer, row, col);
        component.setForeground(Color.BLACK);
        if (col == 2) {    // value col
            VariableRow vRow = getTableModel().getVariableAtRow(row);
            switch (_mode) {
                case Adding:
                    boolean valid = vRow == null || vRow.isValidValue();
                    if (! valid) component.setBackground(Color.PINK);
                    component.setToolTipText(valid ? null : " Invalid value for data type ");
                    break;
                case Replacing:
                    String csv = getCornerstoneValue(vRow.getName());

                    // colour row value blue where cornerstone value != workitem value
                    boolean matches = csv == null || csv.equals(vRow.getValue());
                    if (! matches) component.setForeground(Color.BLUE);
                    component.setToolTipText(matches ? null : " Cornerstone value: " + csv);
                    break;
            }
        }
        return component;
    }


    private String getCornerstoneValue(String name) {
        if (_cornerstoneNode == null) return null;
        XNode rowNode = _cornerstoneNode.getChild(name);
        if (rowNode != null) {
            return rowNode.hasChildren() ? rowNode.toPrettyString() : rowNode.getText();
        }
        return null;
    }


    private void disableMoveOnEnter() {
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                getCellEditor().stopCellEditing();
            }
        });
    }
}
