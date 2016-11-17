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

package org.yawlfoundation.yawl.views.graph;

import javax.swing.table.DefaultTableModel;
import java.util.*;

public class DataViewTableModel extends DefaultTableModel {

    private List<VarRow> _rows;

    private static final String[] COLUMN_LABELS = { "Select", "Variable" };

    private static final int SELECT_COLUMN = 0;
    private static final int VAR_COLUMN = 1;


    public DataViewTableModel(Set<String> vars) {
        super();
        setValues(vars);
    }

    public void setValues(Set<String> values) {
        _rows = new ArrayList<VarRow>();
        for (String value : values) {
            _rows.add(new VarRow(value));
        }
        Collections.sort(_rows);
    }

    public int getColumnCount() {
        return COLUMN_LABELS.length;
    }

    public String getColumnName(int columnIndex) {
        return COLUMN_LABELS[columnIndex];
    }

    public Class getColumnClass(int columnIndex) {
        return columnIndex == SELECT_COLUMN ? Boolean.class : String.class;
    }

    public boolean isCellEditable(int row, int column) {
        return column == SELECT_COLUMN;
    }

    public int getRowCount() {
        return _rows != null ? _rows.size() : 0;
    }


    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if (column == SELECT_COLUMN) {
            _rows.get(row).selected = (Boolean) aValue;
            fireTableCellUpdated(row, column);
        }
    }

    public Object getValueAt(int row, int col) {
        switch (col) {
            case SELECT_COLUMN:  {
                return _rows.get(row).selected;
            }
            case VAR_COLUMN:  {
                return _rows.get(row).name;
            }
            default: {
                return null;
            }
        }
    }


    public Set<String> getSelected() {
        Set<String> selectedSet = new HashSet<String>();
        for (VarRow row : _rows) {
             if (row.selected) {
                 selectedSet.add(row.name);
             }
        }
        return selectedSet;
    }

    public void selectAll(boolean select) {
        for (VarRow row : _rows) {
            row.selected = select;
        }
        fireTableDataChanged();
    }


    class VarRow implements Comparable<VarRow> {

        boolean selected;
        String name;

        VarRow(String name) {
            this.name = name;
            selected = true;
        }


        @Override
        public int compareTo(VarRow o) {
            return this.name.compareTo(o.name);
        }
    }

}