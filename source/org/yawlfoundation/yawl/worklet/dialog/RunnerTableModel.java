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

import org.yawlfoundation.yawl.worklet.selection.WorkletRunner;

import javax.swing.table.AbstractTableModel;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class RunnerTableModel extends AbstractTableModel {

    private java.util.List<WorkletRunner> _runnerList;

    private static final String[] COLUMN_LABELS = {  "Worklet Case", "Worklet Name",
            "Parent Case", "Parent Spec", "Parent Item", "Rule" };


    public RunnerTableModel() { super(); }

    public RunnerTableModel(java.util.List<WorkletRunner> runnerList) {
        super();
        _runnerList = runnerList;
    }


    public int getRowCount() { return _runnerList.size(); }

    public int getColumnCount() { return COLUMN_LABELS.length; }

    public String getColumnName(int column) { return COLUMN_LABELS[column]; }

    public Class<?> getColumnClass(int columnIndex) { return String.class; }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void setValueAt(Object value, int row, int col) { }

    public Object getValueAt(int row, int col) {
        if (row < getRowCount()) {
            WorkletRunner runner = _runnerList.get(row);
            switch (col) {
                case 0: return runner.getCaseID();
                case 1: return runner.getWorkletSpecID().getUri();
                case 2: return runner.getParentCaseID();
                case 3: return runner.getParentSpecID().getUri();
                case 4: return runner.getWorkItemID();
                case 5: return runner.getRuleType().toString();
            }
        }
        return null;
    }


    public void setRows(java.util.List<WorkletRunner> runners) {
        _runnerList = runners;
        fireTableDataChanged();
    }


    public WorkletRunner getRunner(int index) {
        return index > -1 && index < getRowCount() ? _runnerList.get(index) : null;
    }

}
