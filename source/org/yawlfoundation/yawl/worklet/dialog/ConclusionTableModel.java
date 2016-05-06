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

import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.exception.ExletAction;
import org.yawlfoundation.yawl.worklet.exception.ExletTarget;
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class ConclusionTableModel extends AbstractTableModel {

    private RdrConclusion _conclusion;
    private boolean _editable;

    private static final String[] COLUMN_LABELS = { "", "Action", "Target" };


    public ConclusionTableModel(DialogMode mode) {
        super();
        _conclusion = new RdrConclusion();
        _editable = mode != DialogMode.Viewing;
    }


    public void setEditable(boolean editable) { _editable = editable; }


    public int getRowCount() {
        return _conclusion.getCount();
    }


    public int getColumnCount() { return COLUMN_LABELS.length; }


    public String getColumnName(int column) { return COLUMN_LABELS[column]; }


    public Class<?> getColumnClass(int columnIndex) { return String.class; }


    public boolean isCellEditable(int row, int column) {
        return _editable && column > 0;
    }


    public Object getValueAt(int row, int col) {
        if (row < getRowCount()) {
            RdrPrimitive primitive = _conclusion.getPrimitive(row + 1);
            switch (col) {
                case 0: return String.valueOf(row + 1);
                case 1: return getDisplayValue(primitive.getAction());
                case 2: {
                    if (isWorkletAction(primitive.getAction())) {
                        return getWorkletsForView(primitive);
                    }
                    return getDisplayValue(primitive.getTarget());
                }
            }
        }
        return null;
    }


    public void setValueAt(Object value, int row, int col) {
        if (row < getRowCount() && col > 0) {
            RdrPrimitive primitive = _conclusion.getPrimitive(row + 1);
            switch (col) {
                case 1: primitive.setAction(value.toString()); break;
                case 2: primitive.setTarget(value.toString()); break;
            }
            fireTableRowsUpdated(0, row);
        }
    }


    public void setConclusion(RdrConclusion conclusion) {
        _conclusion = conclusion != null ? conclusion : new RdrConclusion();
        fireTableDataChanged();
    }


    public RdrConclusion getConclusion() { return _conclusion; }


    public boolean hasValidContent() {
        for (RdrPrimitive primitive : _conclusion.getPrimitives()) {
            if (! primitive.isValid()) return false;
        }
        return true;
    }


    public void addRow() {
        _conclusion.addPrimitive(ExletAction.Invalid, ExletTarget.Invalid);
        int newRowIndex = getRowCount() - 1;
        fireTableRowsInserted(newRowIndex, newRowIndex);
    }


    public void removeRow(int row) {
        if (getRowCount() == 0 || row >= getRowCount()) return;
        RdrConclusion newConclusion = new RdrConclusion();
        for (int i=0; i < _conclusion.getCount(); i++) {
            if (i != row) {
                RdrPrimitive primitive = _conclusion.getPrimitive(i+1);
                newConclusion.addPrimitive(primitive.getAction(), primitive.getTarget());
            }
        }
        setConclusion(newConclusion);
    }


    // list of worklet names to present to user
    private String getWorkletsForView(RdrPrimitive primitive) {
        List<String> names = getURIs(primitive.getTarget());
        return names.isEmpty() ? getDisplayValue(primitive.getTarget()) :
                StringUtil.join(names, ';');
    }


    private boolean isWorkletAction(String action) {
        return ExletAction.fromString(action).isWorkletAction();
    }


    // a value of "UID..." means the worklet referred to is not currently loaded
    private String getDisplayValue(String value) {
        return value.equals("invalid") ? "<choose>" :
                value.startsWith("UID") ? "*UNLOADED*: " + value : value;
    }


    private List<String> getURIs(String target) {
        return WorkletClient.getInstance().getWorkletCache().getURIsForTarget(target);
    }

}
