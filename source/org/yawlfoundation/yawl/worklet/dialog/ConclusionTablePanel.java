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
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.support.ExletValidationError;
import org.yawlfoundation.yawl.worklet.support.ExletValidator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author Michael Adams
 * @date 30/09/2014
 */
public class ConclusionTablePanel extends JPanel implements ActionListener {

    private final ConclusionTable table;
    private final ErrorMessageShortener msgShortener;
    private StatusPanel status;
    private MiniToolBar toolbar;


    public ConclusionTablePanel(NodePanel parent) {
        super();
        msgShortener = new ErrorMessageShortener();
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Actions"));
        table = new ConclusionTable(parent);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setSize(new Dimension(600, 200));
        add(scrollPane, BorderLayout.CENTER);
        add(populateToolBar(parent), BorderLayout.SOUTH);
    }


    public void setNode(RdrNode ruleNode) {
        setConclusion(ruleNode.getConclusion());
    }


    public void setConclusion(RdrConclusion conclusion) {
        table.setConclusion(conclusion);
        if (conclusion == null || conclusion.isNullConclusion()) {
            status.set("Action required");
        }
        else {
            validateConclusion();
        }
        table.setPreferredScrollableViewportSize(getPreferredSize());
    }


    public RdrConclusion getConclusion() {
        return table.getConclusion();
    }


    public java.util.List<ExletValidationError> validateConclusion() {
        RdrConclusion conclusion = getConclusion();
        java.util.List<ExletValidationError> errors;
        if (conclusion.isNullConclusion()) {
            errors = new ArrayList<ExletValidationError>();
            errors.add(new ExletValidationError(0, "Action(s) required"));
        }
        else {
            errors = new ExletValidator().validate(conclusion,
                    table.getTableModel().getWorkletSpecificationKeys());
        }
        setVisuals(errors);
        return errors;
    }


    public void setVisuals(java.util.List<ExletValidationError> errors) {
        table.setVisuals(errors);
        if (errors.isEmpty()) {
            status.clear();
        }
        else {
            java.util.List<String> msgList = msgShortener.getExletError(
                    errors.get(0).getMessage());
            String shortMsg = msgList.remove(0);
            status.set(shortMsg, msgList);
        }
    }


    public boolean hasValidContent() {
        return table.hasValidContent();
    }


    public void actionPerformed(ActionEvent event) {
        String action = event.getActionCommand();
        if (action.equals("Add")) {
            table.addRow();
        }
        else if (action.equals("Del")) {
            table.removeRow();
            validateConclusion();
        }
    }

    public ConclusionTable getTable() { return table; }


    public void enableButtons(boolean enable) {
        toolbar.enableComponents(enable);
    }


    public void setStatus(String msg) { status.set(msg); }


    private JToolBar populateToolBar(NodePanel parent) {
        toolbar = new MiniToolBar(this);
        toolbar.addButton("plus", "Add", " Add ");
        toolbar.addButton("minus", "Del", " Remove ");
        toolbar.addSeparator(new Dimension(16, 16));
        status = new StatusPanel(parent.getDialog());
        toolbar.add(status);
        return toolbar;
    }

}
