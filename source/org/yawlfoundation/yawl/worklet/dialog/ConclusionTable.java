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

import org.yawlfoundation.yawl.editor.ui.swing.JSingleSelectTable;
import org.yawlfoundation.yawl.worklet.exception.ExletValidationError;
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class ConclusionTable extends JSingleSelectTable {

    NodePanel _parent;                // reference to type combo - affects cell rendering
    java.util.List<ExletValidationError> _errList;


    public ConclusionTable(NodePanel parent, DialogMode mode) {
        super();
        _parent = parent;
        setModel(new ConclusionTableModel(mode));
        getModel().addTableModelListener(parent.getDialog());
        setRowHeight(getRowHeight() + 5);
        setCellSelectionEnabled(true);
        setRowSelectionAllowed(true);
        setColumnSelectionAllowed(true);
        setFillsViewportHeight(true);            // to allow drops on empty table
        getColumnModel().getColumn(1).setCellEditor(new ExletActionCellEditor(parent));
        getColumnModel().getColumn(2).setCellEditor(
                new ExletTargetCellEditor(parent, parent.getDialog()));
        fixSelectorColumn();
    }


    public void addCellEditorListener(CellEditorListener listener) {
        getColumnModel().getColumn(1).getCellEditor().addCellEditorListener(listener);
        getColumnModel().getColumn(2).getCellEditor().addCellEditorListener(listener);
    }


    public void editingStarted() {
        _parent.conclusionEditingStarted();
    }


    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
        JComponent component = (JComponent) super.prepareRenderer(renderer, row, col);
        if (col > 0 && (hasError(row) || hasInvalidValue(component))) {
            component.setBackground(Color.PINK);
        }
        return component;
    }


    public boolean hasValidContent() {
        return getRowCount() > 0 && getTableModel().hasValidContent();
    }

    public RdrConclusion getConclusion() { return getTableModel().getConclusion(); }


    public RdrPrimitive getPrimitiveAtRow(int row) {
        return getConclusion().getPrimitive(row + 1);
    }


    public void setConclusion(RdrConclusion conclusion) {
        getTableModel().setConclusion(conclusion);
        if (conclusion == null || conclusion.isNullConclusion()) {
            _errList = null;    // reset
        }
        updateUI();
    }


    public ConclusionTableModel getTableModel() {
        return (ConclusionTableModel) getModel();
    }


    public RuleType getSelectedRuleType() {
        return _parent.getSelectedRule();
    }


    public void setVisuals(java.util.List<ExletValidationError> errors) {
        _errList = errors;
        invalidate();
    }


    public void addRow() {
        getTableModel().addRow();
        int row = getRowCount() - 1;
        selectRow(row);
        editCellAt(row, 0);
        requestFocusInWindow();
    }

    public void removeRow() {
        int row = getSelectedRow();
        getTableModel().removeRow(row);
        if (getRowCount() > 0) {
            selectRow((row < getRowCount() - 1) ? row : getRowCount() - 1);
        }
    }


    private void fixSelectorColumn() {
        TableColumn column = getColumnModel().getColumn(0);
        column.setPreferredWidth(15);
        column.setMaxWidth(15);
        column.setResizable(false);
    }


    private boolean hasError(int row) {
        if (hasErrors()) {
            for (ExletValidationError error : _errList) {
                int errIndex = error.getIndex();
                if (errIndex == 0 || errIndex == row + 1) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean hasErrors() {
        return ! (_errList == null || _errList.isEmpty());
    }


    private boolean hasInvalidValue(JComponent component) {
        String text = ((DefaultTableCellRenderer.UIResource) component).getText();
        return text.equals("<choose>") || text.isEmpty();
    }

}
