/*
 * Copyright (c) 2004-2015 The YAWL Foundation. All rights reserved.
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

import org.yawlfoundation.yawl.editor.ui.properties.data.StatusPanel;
import org.yawlfoundation.yawl.editor.ui.properties.dialog.component.MiniToolBar;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.exception.ExletValidationError;
import org.yawlfoundation.yawl.worklet.exception.ExletValidator;
import org.yawlfoundation.yawl.worklet.graph.NetDialog;
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class ConclusionTablePanel extends JPanel implements ActionListener {

    private final ConclusionTable _table;
    private final ErrorMessageShortener _msgShortener;
    private StatusPanel _status;
    private MiniToolBar _toolBar;
    private JButton _btnGraph;
    private boolean _shouldValidate;


    public ConclusionTablePanel(NodePanel parent, DialogMode mode) {
        super();
        _msgShortener = new ErrorMessageShortener();
        _shouldValidate = mode != DialogMode.Viewing;
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Actions"));
        _table = new ConclusionTable(parent, mode);
        JScrollPane scrollPane = new JScrollPane(_table);
        scrollPane.setSize(new Dimension(600, 200));
        add(scrollPane, BorderLayout.CENTER);
        add(populateToolBar(parent, mode), BorderLayout.SOUTH);
    }


    public void setNode(RdrNode ruleNode) {
        setConclusion(ruleNode.getConclusion());
    }


    public void setConclusion(RdrConclusion conclusion) {
        _table.setConclusion(conclusion);
        if (conclusion == null || conclusion.isNullConclusion()) {
            if (_shouldValidate) _status.set("Action required");
        }
        else {
            validateConclusion();
        }
        _table.setPreferredScrollableViewportSize(getPreferredSize());
    }


    public RdrConclusion getConclusion() {
        return _table.getConclusion();
    }


    public java.util.List<ExletValidationError> validateConclusion() {
        if (_shouldValidate) {
            RdrConclusion conclusion = getConclusion();
            java.util.List<ExletValidationError> errors;
            if (conclusion.isNullConclusion()) {
                errors = new ArrayList<ExletValidationError>();
                errors.add(new ExletValidationError(0, "Action(s) required"));
            }
            else {
                errors = new ExletValidator().validate(_table.getSelectedRuleType(),
                        conclusion,
                        WorkletClient.getInstance().getWorkletCache().getKeySet());
            }
            setVisuals(errors);
            return errors;
        }
        return Collections.emptyList();
    }


    private void setVisuals(java.util.List<ExletValidationError> errors) {
        _table.setVisuals(errors);
        if (errors.isEmpty()) {
            _status.clear();
        }
        else {
            java.util.List<String> msgList = _msgShortener.getExletError(
                    errors.get(0).getMessage());
            String shortMsg = msgList.remove(0);
            _status.set(shortMsg, msgList);
        }
    }


    public boolean hasValidContent() {
        return _table.hasValidContent();
    }


    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        if (action.equals("Add")) {
            _table.addRow();
        }
        else if (action.equals("Del")) {
            _table.removeRow();
            validateConclusion();
        }
        else if (action.equals("Graph")) {
            NetDialog dialog = new NetDialog(getConclusion(), _table.getSelectedRuleType());
            dialog.setVisible(true);
            RdrConclusion conclusion = dialog.getConclusion();
            if (conclusion != null) {                           // dialog not cancelled
                setConclusion(conclusion);
            }
        }
    }


    public ConclusionTable getTable() { return _table; }


    public void enableButtons(boolean enable) {
        _toolBar.enableComponents(enable);
    }


    public void enableGraphButton(boolean enable) { _btnGraph.setEnabled(enable); }


    public void setStatus(String msg) { _status.set(msg); }


    private JToolBar populateToolBar(NodePanel parent, DialogMode mode) {
        _toolBar = new MiniToolBar(this);
        _toolBar.addButton("plus", "Add", " Add ");
        _toolBar.addButton("minus", "Del", " Remove ");
        _btnGraph = _toolBar.addButton("mapping", "Graph", " Graphical Editor ");
        _toolBar.addSeparator(new Dimension(16, 16));
        _status = new StatusPanel(parent.getDialog());
        _toolBar.add(_status);
        _toolBar.enableComponents(mode != DialogMode.Viewing);
        return _toolBar;
    }

}
