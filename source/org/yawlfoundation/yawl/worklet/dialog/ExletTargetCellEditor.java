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

import org.yawlfoundation.yawl.editor.ui.resourcing.subdialog.ListDialog;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.worklet.exception.ExletAction;
import org.yawlfoundation.yawl.worklet.exception.ExletTarget;
import org.yawlfoundation.yawl.worklet.model.WorkletListModel;
import org.yawlfoundation.yawl.worklet.rdr.RdrPrimitive;
import org.yawlfoundation.yawl.worklet.support.WorkletInfo;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class ExletTargetCellEditor extends ExletCellEditor {

    private final JButton _fldWorklet;               // co-opt a button to show dialog
    private final JLabel _emptyLabel;
    private ExletAction _currentAction;
    private final JDialog _owner;


    public ExletTargetCellEditor(CellEditorListener listener, JDialog owner) {
        super(listener);
        _emptyLabel = new JLabel();
        _fldWorklet = newWorkletField();
        _owner = owner;
    }


    public Object getCellEditorValue() {
        switch (_currentAction) {
            case Invalid: return "<choose>";
            case Compensate:
            case Select: return _fldWorklet.getText();
            default: return _combo.getSelectedItem();
        }
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row,
                                                 int column) {
        super.getTableCellEditorComponent(table, value, isSelected, row, column);
        _currentAction = getActionAtRow(table, row);
        if (_currentAction.isWorkletAction()) {
            _fldWorklet.setText((String) value);
            return _fldWorklet;
        }

        if (_currentAction.isInvalidAction()) {
            return _emptyLabel;     // no editing of target without valid action first
        }

        return newComboInstance(table, value);
    }


    protected Vector<ExletTarget> getItemsForContext(ConclusionTable table) {
        Vector<ExletTarget> targets = new Vector<ExletTarget>();
        if (! _currentAction.isItemOnlyAction()) {
            targets.add(ExletTarget.AllCases);
            targets.add(ExletTarget.AncestorCases);
            targets.add(ExletTarget.Case);
        }
        if (table.getSelectedRuleType().isItemLevelType()) {
            targets.add(ExletTarget.Workitem);
        }
        return targets;
    }


    private ExletAction getActionAtRow(JTable table, int row) {
        RdrPrimitive primitive = ((ConclusionTable) table).getPrimitiveAtRow(row);
        return primitive != null ? primitive.getExletAction() : ExletAction.Invalid;
    }


    private void showListDialog() {
        ListDialog dialog = new ListDialog(_owner, new WorkletListModel(), "Worklets");
        dialog.setResizable(true);
        dialog.setPreferredSize(new Dimension(550, 500));
        dialog.pack();
        dialog.setVisible(true);
        java.util.List<String> selections = new ArrayList<String>();
        for (Object o : dialog.getSelections()) {
             selections.add(((WorkletInfo) o).getSpecID().getKey());
        }
        if (! selections.isEmpty()) {
            _fldWorklet.setText(StringUtil.join(selections, ';'));
        }
        fireEditingStopped();
    }


    private JButton newWorkletField() {
        JButton button = new JButton();
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setBackground(UIManager.getColor("TextField.background"));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showListDialog();
            }
        });

        return button;
    }

}
