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

import org.yawlfoundation.yawl.editor.core.data.YDataHandler;
import org.yawlfoundation.yawl.editor.core.data.YDataHandlerException;
import org.yawlfoundation.yawl.editor.ui.properties.data.binding.AbstractDataBindingDialog;
import org.yawlfoundation.yawl.editor.ui.properties.data.binding.InputBindingDialog;
import org.yawlfoundation.yawl.editor.ui.properties.data.binding.OutputBindingDialog;
import org.yawlfoundation.yawl.editor.ui.properties.data.binding.view.BindingViewDialog;
import org.yawlfoundation.yawl.editor.ui.properties.data.validation.BindingTypeValidator;
import org.yawlfoundation.yawl.editor.ui.properties.data.validation.DataTypeChangeValidator;
import org.yawlfoundation.yawl.editor.ui.properties.dialog.ExtendedAttributesDialog;
import org.yawlfoundation.yawl.editor.ui.properties.dialog.LogPredicateDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @author Michael Adams
 * @date 9/08/12
 */
public class TaskVariableTablePanel extends VariableTablePanel
        implements ActionListener, ListSelectionListener, TableModelListener {

    // toolbar buttons
    private JButton btnInMapping;
    private JButton btnOutMapping;
    private JButton btnAutoMapping;
    private JButton btnQuickView;
    private JButton btnMIVar;
    private JButton btnExAttributes;
    private JButton btnLog;

    private BindingTypeValidator inputBindingValidator;
    private BindingTypeValidator outputBindingValidator;



    public TaskVariableTablePanel(java.util.List<VariableRow> rows,
                                  String decompositionID, DataVariableDialog parent) {
        super(rows, TableType.Task, decompositionID, parent);
        populateToolBar();
        enableButtons(true);
    }



    // table change event
    public void tableChanged(TableModelEvent e) {
        super.tableChanged(e);
        setBindingIconsForSelection();
    }


    public void showMIButton(boolean show) { btnMIVar.setVisible(show); }


    public Vector<VariableScope> getScopes() {
        Vector<VariableScope> scopes = super.getScopes();
        if (! parent.isCompositeTask()) {
            scopes.remove(VariableScope.LOCAL);
        }
        return scopes;
    }


    public void actionPerformed(ActionEvent event) {
        super.actionPerformed(event);
        String action = event.getActionCommand();
        if (action.equals("InBinding")) {
            showBindingDialog(YDataHandler.INPUT);
        }
        else if (action.equals("OutBinding")) {
            showBindingDialog(YDataHandler.OUTPUT);
        }
        else if (action.equals("Autobind")) {
            autoBind();
        }
        else if (action.equals("View")) {
            new BindingViewDialog(this, parent).setVisible(true);
        }
        else if (action.equals("MarkMI")) {
            int row = table.getSelectedRow();
            String error = parent.setMultiInstanceRow(table.getSelectedVariable());
            if (error != null) showErrorStatus(error, null);
            table.selectRow(row);
        }
        else if (action.equals("ExAt")) {
            VariableRow row = table.getSelectedVariable();
            if (row != null) {
                new ExtendedAttributesDialog(parent, row).setVisible(true);
                setTableChanged();                  // to flag update
            }
        }
        else if (action.equals("Log")) {
           showLogPredicateDialog();
        }
    }



    public TaskVariableTablePanel copy() {
        return new TaskVariableTablePanel(table.getVariables(),
                table.getDecompositionID(), parent);
    }


    public void revalidateBindingsInBackground() {
        DataTypeChangeValidator validator = new DataTypeChangeValidator(this);
        validator.revalidateTaskBindingsInBackground();
    }


    private void populateToolBar() {
        btnInMapping = toolbar.addButton("inBinding", "InBinding", " Input Bindings ");
        btnOutMapping = toolbar.addButton("outBinding", "OutBinding", " Output Bindings ");
        btnQuickView = toolbar.addButton("view", "View", " Quick View Bindings ");
        btnAutoMapping = toolbar.addButton("generate", "Autobind", " Smart Data Bindings ");
        btnExAttributes = toolbar.addButton("exat", "ExAt", " Ext. Attributes ");
        btnLog = toolbar.addButton("log", "Log", " Log Entries ");
        btnMIVar = toolbar.addButton("miVar", "MarkMI", " Mark as MI ");
        addStatusBar();
    }


    private void showBindingDialog(int scope) {
        int selectedRow = table.getSelectedRow();
        VariableRow selectedVar = table.getSelectedVariable();
        java.util.List<VariableRow> netVars =
                parent.getNetTablePanel().getTable().getVariables();
        java.util.List<VariableRow> taskVars = table.getVariables();
        String taskID = parent.getTask().getID();
        AbstractDataBindingDialog dialog = null;

        MultiInstanceHandler miHandler = table.hasMultiInstanceRow() ?
            parent.getMultiInstanceHandler() : null;

        if (scope == YDataHandler.INPUT) {
            if (inputBindingValidator == null) {
                inputBindingValidator = new BindingTypeValidator(netVars, "string");
            }
            dialog = new InputBindingDialog(taskID, selectedVar,
                    netVars, taskVars, miHandler, inputBindingValidator);
        }
        else if (scope == YDataHandler.OUTPUT) {
            if (outputBindingValidator == null) {
                outputBindingValidator = new BindingTypeValidator(taskVars, "string");
            }
            dialog = new OutputBindingDialog(taskID, selectedVar, netVars, taskVars,
                    parent.getOutputBindings(), miHandler, outputBindingValidator);
        }
        if (dialog != null) {
            dialog.setVisible(true);
            if (dialog.hasChanges() || selectedVar.isBindingChange()) {
                parent.enableApplyButton();
                table.getTableModel().setTableChanged(true);
            }
            table.getTableModel().fireTableDataChanged();
        }
        table.selectRow(selectedRow);
    }


    private void showLogPredicateDialog() {
        VariableRow row = table.getSelectedVariable();
        if (row != null) {
            LogPredicateDialog dialog = new LogPredicateDialog(row);
            dialog.setVisible(true);
            if (dialog.isUpdated()) {
                row.setLogPredicate(dialog.getUpdatedPredicate());
                setTableChanged();
            }
        }
    }


    protected void enableButtons(boolean enable) {
        super.enableButtons(enable);
        VariableRow row = table.getSelectedVariable();
        boolean hasRowSelected = table.getSelectedRow() > -1;
        btnInMapping.setEnabled(enable && hasRowSelected &&
                row != null && (row.isInput() || row.isInputOutput()));
        btnOutMapping.setEnabled(enable && hasRowSelected &&
                row != null && (row.isOutput() || row.isInputOutput()));
        btnExAttributes.setEnabled(enable && hasRowSelected);
        btnMIVar.setEnabled(enable && shouldEnableMIButton());
        btnAutoMapping.setEnabled(enable && shouldEnableAutoBindingButton());
        btnQuickView.setEnabled(enable && hasRowSelected);
        btnLog.setEnabled(enable && hasRowSelected);
    }


    private boolean shouldEnableMIButton() {
        VariableRow row = table.getSelectedVariable();

        // MI button can enable if the row is already MI (to allow toggling) or
        // there's no current MI row AND the row's data type is MI valid
        return row != null && (row.isMultiInstance() ||
                ( ! table.hasMultiInstanceRow() && isValidMIType(row.getDataType())));
    }


    // The auto bind button should be enabled if there is a row that has no mappings
    // and there is a net-var with the same name and data type as that row
    private boolean shouldEnableAutoBindingButton() {
        java.util.List<VariableRow> netVars =
                parent.getNetTablePanel().getTable().getVariables();
        if (netVars.isEmpty()) return false;

        for (VariableRow row : table.getVariables()) {
            if (!hasBinding(row)) {
                for (VariableRow netVar : netVars) {
                    if (row.equalsNameAndType(netVar)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private boolean hasBinding(VariableRow row) {
        return ((row.isInput() || row.isInputOutput()) && row.getBinding() != null) ||
               ((row.isOutput() || row.isInputOutput()) &&
                       parent.getOutputBindings().hasBinding(row.getName()));
    }


    private boolean isValidMIType(String dataType) {
        try {
            return getDataHandler().getMultiInstanceItemNameAndType(dataType) != null;
        }
        catch (YDataHandlerException ydhe) {
            return false;
        }
    }


    private void autoBind() {
        boolean changed = false;
        int selectedRow = table.getSelectedRow();
        for (VariableRow row : table.getVariables()) {
            if (! hasBinding(row)) {
                boolean success = parent.createAutoBinding(row);
                if (success) {
                    row.setValidBindings();
                }
                changed = success || changed;
            }
        }
        if (changed) table.getTableModel().fireTableDataChanged();
        table.selectRow(selectedRow);
    }


    protected void setEditMode(boolean editing) {
        if (isEditing != editing) {
            super.setEditMode(editing);
            if (! editing && tableType == TableType.Task) {
                    showBindingStatus(getTable().getSelectedVariable());
            }
        }
    }


    public void setBindingIconsForSelection() {
        VariableRow selection = getTable().getSelectedVariable();
        if (selection != null) {
            setBindingIcons(selection);
            parent.enableButtonsIfValid();
        }
    }



    protected void setBindingIcons(VariableRow row) {
        if (row != null) {
            setInputBindingIcon(row.isValidInputBinding());
            setOutputBindingIcon(row.isValidOutputBinding());
            showBindingStatus(row);
        }
    }


    protected void setInputBindingIcon(boolean valid) {
        if (btnInMapping != null) {
            toolbar.setIcon(btnInMapping, valid ? "inBinding" : "inBindingInvalid");
            btnInMapping.setToolTipText(valid ? " Input Bindings " :
                    " Input Bindings (invalid) ");
        }
    }


    protected void setOutputBindingIcon(boolean valid) {
        if (btnOutMapping != null) {
            toolbar.setIcon(btnOutMapping, valid ? "outBinding" : "outBindingInvalid");
            btnOutMapping.setToolTipText(valid ? " Output Bindings " :
                    " Output Bindings (invalid) ");
        }
    }


    protected void showBindingStatus(VariableRow row) {
        if (row.isAdding()) return;

        boolean invalidInput = ! row.isValidInputBinding();
        boolean invalidOutput = ! row.isValidOutputBinding();

        if (invalidInput && invalidOutput) {
            showErrorStatus("Invalid or empty bindings", null);
        }
        else if (invalidInput) {
            showErrorStatus("Invalid or empty input binding", null);
        }
        else if (invalidOutput) {
            showErrorStatus("Invalid output binding", null);
        }
        else clearStatus();
    }


    protected void notifyUsageChange(int usage) {
        VariableRow row = table.getSelectedVariable();
        int oldUsage = row.getUsage();
        if (oldUsage == YDataHandler.INPUT_OUTPUT || oldUsage == usage) return;

        // now, old != new and old is INPUT ONLY or OUTPUT ONLY
        parent.createBinding(row, usage);
    }

}
