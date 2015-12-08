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

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.elements.model.AtomicTask;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.elements.YAWLServiceGateway;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * @author Michael Adams
 * @date 29/09/2014
 */
public class AddRuleDialog extends AbstractNodeDialog
        implements ActionListener, ListSelectionListener, CellEditorListener {

    private JButton _btnAdd;
    private JButton _btnClose;
    private NodePanel _nodePanel;


    public AddRuleDialog() {
        super(YAWLEditor.getInstance());
        setTitle("Add Worklet Rule for Specification: " +
                SpecificationModel.getHandler().getID());
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(getContent());
        setPreferredSize(new Dimension(800, 540));
        setMinimumSize(new Dimension(800, 540));
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    // ActionListener on ButtonBar
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();
        if (cmd.equals("Cancel")) {
            setVisible(false);
        }
        else if (cmd.equals("Clear")) {
            clearInputs();
        }
        else if (cmd.equals("Add Rule")) {
            addRule();
        }
        else if (cmd.equals("Add & Close")) {
            addRule();
            setVisible(false);
        }
    }


    // ListSelectionListener on nodePanel data table selection
    public void valueChanged(ListSelectionEvent event) {
        if (! event.getValueIsAdjusting()) {
            enableButtons();
        }
    }


    // CellEditorListener on nodePanel data table value edit
    public void editingStopped(ChangeEvent e) {
        enableButtons();
    }


    // CellEditorListener on nodePanel data table value edit cancel
    public void editingCanceled(ChangeEvent e) { }


    public void enableButtons() {
        if (_nodePanel != null) {
            boolean shouldEnable = _nodePanel.hasValidContent();
            _btnAdd.setEnabled(shouldEnable);
            _btnClose.setEnabled(shouldEnable);
        }
    }


    private JPanel getContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.add(getNodePanel(), BorderLayout.CENTER);
        panel.add(getButtonPanel(), BorderLayout.SOUTH);
        return panel;
    }


    private JPanel getNodePanel() {
        _nodePanel = new NodePanel(getSelectedTask(), this);
        _nodePanel.addConclusionTableCellEditorListener(this);
        _nodePanel.addDataContextEventListener(this);
        return _nodePanel;
    }


    private JPanel getButtonPanel() {
        ButtonPanel panel = new ButtonPanel();
        panel.setBorder(new EmptyBorder(5,5,10,5));
        panel.addButton("Cancel", this);
        panel.addButton("Clear", this);
        _btnAdd = panel.addButton("Add Rule", this);
        _btnClose = panel.addButton("Add & Close", this);
        _btnAdd.setEnabled(false);
        _btnClose.setEnabled(false);
        panel.add(_btnClose);
        return panel;
    }


    private void addRule() {
        YSpecificationID specID = SpecificationModel.getHandler()
                .getSpecification().getSpecificationID();
        RdrNode node = _nodePanel.getRdrNode();
        RuleType rule = _nodePanel.getSelectedRule();
        AtomicTask task = _nodePanel.getSelectedTask();
        String taskID = task != null ? task.getID() : null;

        WorkletClient client = new WorkletClient();
        try {
            String result = client.addRule(specID, taskID, rule, node);
            if (client.successful(result)) {
                MessageDialog.info("Rule successfully added.", "Add Worklet Rule");
            }
            else {
                MessageDialog.error(StringUtil.unwrap(result), "Add Worklet Rule");
            }
        }
        catch (IOException ioe) {
            MessageDialog.error(ioe.getMessage(), "Add Worklet Rule");
        }

        // also add service to task for worklet selection
        if (task != null && rule == RuleType.ItemSelection) {
            YAWLServiceReference service = getServiceReference();
            if (service != null) {
                YDecomposition decomposition = getOrCreateDecomposition(task);
                if (decomposition != null) {
                    ((YAWLServiceGateway) decomposition).setYawlService(service);
                }
            }
        }
        clearInputs();
    }


    private void clearInputs() {
        _btnAdd.setEnabled(false);
        _btnClose.setEnabled(false);
        _nodePanel.clearInputs();
    }

}
