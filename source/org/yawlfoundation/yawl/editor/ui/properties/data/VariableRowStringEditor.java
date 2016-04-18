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

import org.apache.xerces.util.XMLChar;
import org.yawlfoundation.yawl.editor.core.data.YDataHandlerException;
import org.yawlfoundation.yawl.editor.ui.properties.data.validation.DataTypeChangeValidator;
import org.yawlfoundation.yawl.editor.ui.properties.data.validation.VariableValueDialog;
import org.yawlfoundation.yawl.editor.ui.properties.dialog.component.ValueField;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.util.StringUtil;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * @author Michael Adams
 */
public class VariableRowStringEditor extends DefaultCellEditor
        implements TableCellEditor, ActionListener, PopupMenuListener {

    private JComboBox dataTypeCombo;
    private JCheckBox checkBox;
    private ValueField valuePanel;

    private VariableTablePanel tablePanel;
    private DataTypeChangeValidator dataTypeChangeValidator;
    private String editingColumnName;
    private int editingRow;
    private Vector<String> dataTypeNames;


    public VariableRowStringEditor() {
        super(new JTextField());
        setClickCountToStart(1);
    }


    public VariableRowStringEditor(VariableTablePanel panel) {
        this();
        setTablePanel(panel);
    }


    public void setTablePanel(VariableTablePanel panel) { tablePanel = panel; }


    public Object getCellEditorValue() {
        if (editingColumnName.equals("Name")) return super.getCellEditorValue();
        if (editingColumnName.equals("Type")) return dataTypeCombo.getSelectedItem();
        if (editingColumnName.endsWith("Value")) {
            return isBooleanValueRow() ? String.valueOf(checkBox.isSelected())
                    : valuePanel.getText();
        }
        return null;
    }


    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row,
                                                 int column) {
        tablePanel.setEditMode(true);
        editingColumnName = table.getColumnName(column);
        editingRow = row;

        if (editingColumnName.equals("Type")) {
            dataTypeCombo = new JComboBox(getDataTypeNames());
            dataTypeCombo.addPopupMenuListener(this);
            dataTypeCombo.setSelectedItem(value);
            return dataTypeCombo;
        }
        else if (editingColumnName.endsWith("Value")) {
            if (isBooleanValueRow()) {
                checkBox = new JCheckBox();
                checkBox.addActionListener(this);
                checkBox.setSelected(Boolean.valueOf((String) value));
                return checkBox;
            }
            valuePanel = new ValueField(this, null);
            valuePanel.setText((String) value);
            return valuePanel;
        }
        else {      // name field
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
    }


    public boolean stopCellEditing() {
        if (isValid()) {
            super.stopCellEditing();                    // fire stop editing event
            tablePanel.setEditMode(false);              // ...before notifying panel
            return true;
        }
        return false;
    }


    @Override
    public void cancelCellEditing() {
        super.cancelCellEditing();
        tablePanel.cancelEdit();
    }


    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("ShowDialog")) {
            showValueDialog((String) getCellEditorValue());
        }
        if (isValid()) {
            stopCellEditing();
        }
    }


    // these event handlers are for the data type combo
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        stopCellEditing();       // validate
    }

    public void popupMenuCanceled(PopupMenuEvent e) {
        super.stopCellEditing();      // don't validate
    }


    private void showValueDialog(String value) {
        VariableRow row = tablePanel.getVariableAtRow(editingRow);
        VariableValueDialog dialog = new VariableValueDialog(
                tablePanel.getVariableDialog(), row, value);
        String text = dialog.showDialog();
        if (text != null) {
            valuePanel.setText(text);
        }
    }


    private boolean isBooleanValueRow() {
        VariableRow varRow = tablePanel.getVariableAtRow(editingRow);
        return varRow.getDataType().equals("boolean");
    }

    private boolean isValid() {
        String value = (String) getCellEditorValue();
        VariableRow row = tablePanel.getVariableAtRow(editingRow);
        if (editingColumnName.equals("Name")) {
            row.setValidName(validateName(value));
            if (! row.isValidName()) return false;
            tablePanel.clearStatus();
            tablePanel.getVariableDialog().updateMappingsOnVarNameChange(row, value);
        }
        else if (editingColumnName.equals("Type")) {
            if (dataTypeChangeValidator == null) {
                dataTypeChangeValidator = new DataTypeChangeValidator(tablePanel);
            }
            dataTypeChangeValidator.validateBindings(row, value);  // value is data type
        }
        else if (editingColumnName.endsWith("Value")) {
            row.setValidValue(validateValue(value));
            if (! row.isValidValue()) return false;
            tablePanel.clearStatus();
        }

        return true;
    }


    private boolean validateName(String name) {
        String errMsg = null;
        if (name.length() == 0) {
            errMsg = "Name can't be empty";
        }
        else if (! isUniqueName(name)) {
            errMsg = "Duplicate variable name";
        }
        else if (! XMLChar.isValidName(name)) {
            errMsg = "Invalid variable name (not XML valid)";
        }
        if (errMsg != null) {
            tablePanel.showErrorStatus(errMsg, null);
        }
        return errMsg == null;
    }


    private boolean validateValue(String value) {
        return validate(tablePanel.getVariableAtRow(editingRow).getDataType(), value);
    }


    private boolean validate(String dataType, String value) {
        if (StringUtil.isNullOrEmpty(value)) return true;

        try {
            java.util.List<String> errors = SpecificationModel.getHandler()
                    .getDataHandler().validate(dataType, value);
            if (!errors.isEmpty()) {
                tablePanel.showErrorStatus("Invalid value for data type", errors);
            }
            return (errors.isEmpty());
        }
        catch (YDataHandlerException ydhe) {
            tablePanel.showErrorStatus(ydhe.getMessage(), null);
            return false;
        }
    }


    private boolean isUniqueName(String name) {
        VariableTableModel model = tablePanel.getTable().getTableModel();
        for (int i=0; i< model.getRowCount(); i++) {
            if (i != editingRow && name.equals(model.getVariableAtRow(i).getName())) {
                return false;
            }
        }
        return true;
    }


    private Vector<String> getDataTypeNames() {
        if (dataTypeNames == null) {
            try {
                dataTypeNames = new Vector<String>(
                        SpecificationModel.getHandler().getDataHandler().getDataTypeNames());
            }
            catch (YDataHandlerException ydhe) {
                return new Vector<String>();
            }
        }
        return dataTypeNames;
    }

}
