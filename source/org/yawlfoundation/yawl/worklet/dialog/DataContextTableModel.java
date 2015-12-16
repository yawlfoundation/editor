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

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class DataContextTableModel extends AbstractTableModel {

    private List<VariableRow> _variables;
    private boolean _editable;


    private static final String[] COLUMN_LABELS = { "Name", "Type", "Value" };


    public DataContextTableModel() {
        super();
        _editable = true;
    }


    public void setVariables(List<VariableRow> varList) {
        _variables = varList;
        fireTableDataChanged();
    }


    public void setEditable(boolean editable) { _editable = editable; }


    public int getRowCount() {
        return (getVariables() != null) ? getVariables().size() : 0;
    }


    public int getColumnCount() { return COLUMN_LABELS.length; }


    public String getColumnName(int column) { return COLUMN_LABELS[column]; }


    public Class<?> getColumnClass(int columnIndex) { return String.class; }


    public boolean isCellEditable(int row, int column) {
        return _editable && column == 2;    // Value
    }


    public Object getValueAt(int row, int col) {
        VariableRow selected = getVariableAtRow(row);
        if (selected != null) {
            switch (col) {
                case 0: return selected.getName();
                case 1: return selected.getDataType();
                case 2: return selected.getValue();
            }
        }
        return null;
    }


    public void setValueAt(Object value, int row, int col) {
        if (col != 2) return;
        VariableRow selected = getVariableAtRow(row);
        if (selected != null) {
            selected.setValue((String) value);
            fireTableRowsUpdated(row, row);
        }
    }


    public List<VariableRow> getVariables() { return _variables; }


    public VariableRow getVariableAtRow(int row) {
        int count = getRowCount();
        return count == 0 || row >= count ? null : _variables.get(row);
    }

}
