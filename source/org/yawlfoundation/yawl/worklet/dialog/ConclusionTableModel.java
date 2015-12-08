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

import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.exception.ExletAction;
import org.yawlfoundation.yawl.worklet.exception.ExletTarget;
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;
import org.yawlfoundation.yawl.worklet.support.WorkletInfo;

import javax.swing.table.AbstractTableModel;
import java.io.IOException;
import java.util.*;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class ConclusionTableModel extends AbstractTableModel {

    private List<RdrPrimitive> _primitives;
    private Map<RdrPrimitive, List<YSpecificationID>> _workletTargets;
    private final List<WorkletInfo> _infoList = getWorkletList();


    private static final String[] COLUMN_LABELS = { "", "Action", "Target" };


    public ConclusionTableModel() {
        super();
        _primitives = new ArrayList<RdrPrimitive>();
        _workletTargets = new HashMap<RdrPrimitive, List<YSpecificationID>>();
    }


    public void setConclusion(List<RdrPrimitive> primitives) {
        if (primitives != null) {
            _primitives = primitives;
            setWorkletTargets();
        }
        else {
            _primitives.clear();
        }
        fireTableDataChanged();
    }


    public int getRowCount() {
        return (_primitives != null) ? _primitives.size() : 0;
    }


    public int getColumnCount() { return COLUMN_LABELS.length; }


    public String getColumnName(int column) { return COLUMN_LABELS[column]; }


    public Class<?> getColumnClass(int columnIndex) { return String.class; }


    public boolean isCellEditable(int row, int column) {
        return column > 0;
    }


    public Object getValueAt(int row, int col) {
        if (row < getRowCount()) {
            RdrPrimitive primitive =  _primitives.get(row);
            switch (col) {
                case 0: return String.valueOf(row + 1);
                case 1: return primitive.getAction();
                case 2: {
                    if (isWorkletAction(primitive.getAction())) {
                        return getWorkletsForView(primitive);
                    }
                    String target = primitive.getTarget();
                    return target.equals("invalid") ? "" : target;
                }
            }
        }
        return null;
    }


    public void setValueAt(Object value, int row, int col) {
        if (row < getRowCount() && col > 0) {
            RdrPrimitive primitive =  _primitives.get(row);
            switch (col) {
                case 1: primitive.setAction(value.toString()); break;
                case 2: {
                    primitive.setTarget(value.toString());
                    if (isWorkletAction(primitive.getAction())) {
                        addWorkletTarget(primitive);
                    }
                } break;
            }
            fireTableRowsUpdated(row, row);
        }
    }


    public boolean hasValidContent() {
        for (RdrPrimitive primitive : _primitives) {
            if (! primitive.isValid()) return false;
        }
        return true;
    }


    public RdrConclusion getConclusion() {
        RdrConclusion conclusion = new RdrConclusion();
        if (getRowCount() > 0) {                              // not null or empty
            for (RdrPrimitive primitive : _primitives) {
                String action = primitive.getAction();
                if (action.equals(ExletAction.Select.toString())) {
                    conclusion.setSelectionPrimitive(_workletTargets.get(primitive));
                    break;
                }
                else if (action.equals(ExletAction.Compensate.toString())) {
                    conclusion.addCompensationPrimitive(_workletTargets.get(primitive));
                }
                else if (! action.equals(ExletAction.Invalid.toString())) {
                    conclusion.addPrimitive(action, primitive.getTarget());
                }
            }
        }
        return conclusion;
    }


    public void addRow() {
        _primitives.add(new RdrPrimitive(getRowCount(),
                ExletAction.Invalid, ExletTarget.Invalid));
        int newRowIndex = getRowCount() - 1;
        fireTableRowsInserted(newRowIndex, newRowIndex);
    }


    public void removeRow(int row) {
        if (getRowCount() == 0 || row >= getRowCount()) return;
        _primitives.remove(row);
        fireTableDataChanged();
    }


    // list of worklet names to present to user
    private String getWorkletsForView(RdrPrimitive primitive) {
        List<YSpecificationID> workletIDs = _workletTargets.get(primitive);
        if (workletIDs != null) {
            StringBuilder s = new StringBuilder();
            for (YSpecificationID specID : workletIDs) {
                if (s.length() > 0) s.append(';');
                s.append(specID.getUri());
            }
            return s.toString();
        }
        return primitive.getTarget();
    }


    // converts from spec keys to names for display to user
    private void setWorkletTargets() {
        for (RdrPrimitive primitive : _primitives) {
            if (isWorkletAction(primitive.getAction())) {
                addWorkletTarget(primitive);
            }
        }
    }


    private void addWorkletTarget(RdrPrimitive primitive) {
        List<YSpecificationID> workletIDs = new ArrayList<YSpecificationID>();
        for (String target : extractKeysFromTarget(primitive.getWorkletTarget())) {
            for (WorkletInfo info : _infoList) {
                YSpecificationID specID = info.getSpecID();
                if (target.equals(specID.getKey())) {
                    workletIDs.add(specID);
                }
            }
        }
        _workletTargets.put(primitive, workletIDs);
    }


    private List<String> extractKeysFromTarget(String target) {
        String[] keys = target.split(";");
        for (int i=0; i < keys.length; i++) {
             keys[i] = keys[i].trim();
        }
        return Arrays.asList(keys);
    }


    private List<WorkletInfo> getWorkletList() {
        try {
            return new WorkletClient().getWorkletInfoList();
        }
        catch (IOException ioe) {
            return Collections.emptyList();
        }
    }



    private boolean isWorkletAction(String action) {
        return ExletAction.fromString(action).isWorkletAction();
    }

}
