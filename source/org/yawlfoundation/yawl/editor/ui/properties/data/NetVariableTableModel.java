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

/**
* @author Michael Adams
* @date 3/08/12
*/
class NetVariableTableModel extends VariableTableModel {

    private static final String[] COLUMN_LABELS =
            {"", "Name", "Type", "Scope", "Initial Value"};

    protected static final String SELECTOR_INDICATOR = "\u25B6";      // right arrowhead
    protected static final String MI_INDICATOR = "\u2750";            // like an MI task

    protected static final int SELECTOR_COLUMN  = 0;
    protected static final int NAME_COLUMN  = 1;
    protected static final int TYPE_COLUMN  = 2;
    protected static final int SCOPE_COLUMN = 3;
    protected static final int VALUE_COLUMN = 4;

    public NetVariableTableModel() {
        super();
    }

    public int getColumnCount() {
        return COLUMN_LABELS.length;
    }

    public String getColumnName(int column) {
        return COLUMN_LABELS[column];
    }

    public Class<?> getColumnClass(int columnIndex) {
        return (columnIndex == SCOPE_COLUMN) ? Integer.class: String.class;
    }

    public boolean isCellEditable(int row, int column) {
        VariableRow varRow = getVariableAtRow(row);
        return column != SELECTOR_COLUMN && editable &&
               (column != VALUE_COLUMN || varRow.mayUpdateValue()) &&
               (column != TYPE_COLUMN || varRow.mayModifyType());
    }

    public Object getValueAt(int row, int col) {
        VariableRow selected = getVariableAtRow(row);
        switch (col) {
            case SELECTOR_COLUMN: return selected.isMultiInstance() ?
                                             MI_INDICATOR : SELECTOR_INDICATOR;
            case NAME_COLUMN:     return selected.getName();
            case TYPE_COLUMN:     return selected.getDataType();
            case SCOPE_COLUMN:    return VariableScope.getScope(selected.getUsage());
            case VALUE_COLUMN:    return selected.getValue();
            default: return null;
        }
    }

    public void setValueAt(Object value, int row, int col) {
        VariableRow selected = getVariableAtRow(row);
        switch (col) {
            case NAME_COLUMN:  {
                selected.setName((String) value); break;
            }
            case TYPE_COLUMN:  {
                selected.setDataType((String) value); break;
            }
            case SCOPE_COLUMN:  {
                selected.setUsage(((VariableScope) value).getValue()); break;
            }
            case VALUE_COLUMN:  {
                selected.setValue((String) value); break;
            }
        }
        fireTableRowsUpdated(row, row);
    }


    public void addRow() { super.addRow(YDataHandler.LOCAL); }


    public boolean allRowsValid() {
        if (getVariables() != null) {
            for (VariableRow row : getVariables()) {
                if (! row.canBeDropped()) return false;   // i.e. invalid name and/or value
            }
        }
        return true;
    }


}
