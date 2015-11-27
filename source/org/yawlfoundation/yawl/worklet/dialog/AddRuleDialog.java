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

import org.jdom2.Element;
import org.yawlfoundation.yawl.editor.core.YConnector;
import org.yawlfoundation.yawl.editor.core.controlflow.YControlFlowHandlerException;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.elements.model.AtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.ui.net.NetGraphModel;
import org.yawlfoundation.yawl.editor.ui.net.utilities.NetUtilities;
import org.yawlfoundation.yawl.editor.ui.properties.data.VariableRow;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.elements.YAWLServiceGateway;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.elements.data.YVariable;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.schema.XSDType;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;
import org.yawlfoundation.yawl.worklet.support.ExletValidationError;
import org.yawlfoundation.yawl.worklet.support.ExletValidator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

/**
 * @author Michael Adams
 * @date 29/09/2014
 */
public class AddRuleDialog extends JDialog
        implements ActionListener, ItemListener, ListSelectionListener, CellEditorListener {

    private JButton _btnAdd;
    private JButton _btnClose;
    private JTextArea _txtDescription;
    private JComboBox _cbxType;
    private JComboBox _cbxTask;
    private JLabel _cbxTaskPrompt;
    private JTextField _txtCondition;
    private ConclusionTablePanel _conclusionPanel;
    private DataContextTablePanel _dataContextPanel;
    private JTextArea _txtStatus;


    public AddRuleDialog(AtomicTask task) {
        super(YAWLEditor.getInstance());
        setTitle("Add Worklet Rule for Specification: " +
                SpecificationModel.getHandler().getID());
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(getContent(task));
        setPreferredSize(new Dimension(800, 540));
        setMinimumSize(new Dimension(800, 540));
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


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


    // combo selection
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            java.util.List<VariableRow> variables = null;
            Object item = event.getItem();

            // if rule change
            if (item instanceof RuleType) {
                RuleType selectedType = (RuleType) item;
                enabledTaskCombo(selectedType);

                if (selectedType.isCaseLevelType()) {
                    variables = getDataContext(null);  // net level vars
                }
                else {
                    AtomicTask task = (AtomicTask) _cbxTask.getSelectedItem();
                    if (task != null) {
                        variables = getDataContext(task);
                    }
                }
                updateStatus("Rule Type selection changed.");
            }
            else {    // task combo
                variables = getDataContext((AtomicTask) item);
                updateStatus("Task selection changed.");
            }
            _dataContextPanel.setVariables(variables);
            clearInputs();
        }
    }


    // data table selection
    public void valueChanged(ListSelectionEvent event) {
        if (! event.getValueIsAdjusting()) {
            updateCondition(_dataContextPanel.getSelectedVariable());
            validateAddButtonsEnablement();
        }
    }


    // data table value edit
    public void editingStopped(ChangeEvent e) {
        if (e.getSource() instanceof ExletCellEditor) {
            validateConclusion();
        }
        else {
            updateCondition(_dataContextPanel.getSelectedVariable());
        }
        validateAddButtonsEnablement();
    }


    // data table value edit cancel
    public void editingCanceled(ChangeEvent e) { }


    protected void validateAddButtonsEnablement() {
        boolean shouldEnable = _dataContextPanel.hasValidContent() &&
                _conclusionPanel.hasValidContent() &&
                ((ConditionVerifier) _txtCondition.getInputVerifier()).hasValidContent();
        _btnAdd.setEnabled(shouldEnable);
        _btnClose.setEnabled(shouldEnable);
    }

    private JPanel getContent(AtomicTask task) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.add(getEntryPanel(task), BorderLayout.CENTER);
        panel.add(getButtonBar(this), BorderLayout.SOUTH);
        return panel;
    }


    private JPanel getEntryPanel(AtomicTask task) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getStatusPanel(), BorderLayout.SOUTH);
        panel.add(getActionPanel(task), BorderLayout.WEST);
        panel.add(getDataPanel(task), BorderLayout.CENTER);
        return panel;
    }


    private JPanel getActionPanel(AtomicTask task) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getRulePanel(task), BorderLayout.NORTH);
        panel.add(getDescriptionPanel(), BorderLayout.SOUTH);
        panel.add(getConclusionPanel(), BorderLayout.CENTER);
        return panel;
    }


    private JPanel getStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Messages"));
        _txtStatus = new JTextArea(4, 20);
        _txtStatus.setLineWrap(true);
        _txtStatus.setWrapStyleWord(true);
        _txtStatus.setEditable(false);
        _txtStatus.setBackground(new Color(246, 246, 246));
        panel.add(new JScrollPane(_txtStatus), BorderLayout.CENTER);
        return panel;
    }


    private JPanel getDataPanel(AtomicTask task) {
        _dataContextPanel = new DataContextTablePanel(this);
        _dataContextPanel.setVariables(getDataContext(task));
        return _dataContextPanel;
    }


    private JPanel getRulePanel(AtomicTask task) {
        JPanel panel = new JPanel(new SpringLayout());
        _cbxTask = getTaskCombo(task);
        _cbxType = getTypeCombo();
        addContent(panel, "Rule Type:", _cbxType);
        _cbxTaskPrompt = addContent(panel, "Task:", _cbxTask);
        _txtCondition = getConditionField();
        addContent(panel, "Condition:", _txtCondition);
        SpringUtil.makeCompactGrid(panel, 3, 2, 6, 6, 8, 8);

        if (isWorkletTask(task)) {
            _cbxType.setSelectedItem(RuleType.ItemSelection);
        }

        return panel;
    }


    private JLabel addContent(JPanel panel, String prompt, Component c) {
        JLabel label = new JLabel(prompt, JLabel.LEADING);
        label.setFont((Font) UIManager.get("TitledBorder.font"));
        label.setForeground((Color) UIManager.get("TitledBorder.titleColor"));
        panel.add(label);
        label.setLabelFor(c);
        panel.add(c);
        return label;
    }


    private JComboBox getTypeCombo() {
        JComboBox combo = new JComboBox(RuleType.values());

        combo.setRenderer(new ListCellRenderer() {
            protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            public Component getListCellRendererComponent(JList jList, Object o,
                                                          int i, boolean b, boolean b1) {
                JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(
                        jList, o, i, b, b1);
                label.setText(((RuleType) o).toLongString());
                return label;
            }
        });

        combo.addItemListener(this);
        return combo;
    }


    private JComboBox getTaskCombo(AtomicTask task) {
        Vector<YAWLAtomicTask> taskVector = new Vector<YAWLAtomicTask>();
        for (NetGraphModel model : SpecificationModel.getNets()) {
            for (YAWLAtomicTask netTask : NetUtilities.getAtomicTasks(model)) {
                 taskVector.add(netTask);
            }
        }
        JComboBox combo = new JComboBox(taskVector);

        combo.setRenderer(new ListCellRenderer() {
            protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            public Component getListCellRendererComponent(JList jList, Object o,
                                                          int i, boolean b, boolean b1) {
                JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(
                        jList, o, i, b, b1);
                label.setText(o != null ? ((YAWLAtomicTask) o).getLabel() : null);
                return label;
            }
        });

        if (task != null) {
            combo.setSelectedItem(task);
        }
        combo.addItemListener(this);
        combo.setEnabled(false);                    // initially pre-case rule selected
        return combo;
    }


    private JTextField getConditionField() {
        final JTextField field = new JTextField();
        field.setInputVerifier(new ConditionVerifier(this));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                field.setBackground(Color.WHITE);
                field.setToolTipText(null);
            }
        });
        return field;
    }

    private JPanel getDescriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Description (optional)"));
        _txtDescription = new JTextArea(4, 20);
        _txtDescription.setLineWrap(true);
        _txtDescription.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(_txtDescription);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }


    private JPanel getConclusionPanel() {
        _conclusionPanel = new ConclusionTablePanel(_cbxType, this);
        return _conclusionPanel;
    }


    protected JPanel getButtonBar(ActionListener listener) {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5,5,10,5));
        panel.add(createButton("Cancel", listener));
        panel.add(createButton("Clear", listener));
        _btnAdd = createButton("Add Rule", listener);
        _btnAdd.setEnabled(false);
        panel.add(_btnAdd);
        _btnClose = createButton("Add & Close", listener);
        _btnClose.setEnabled(false);
        panel.add(_btnClose);
        return panel;
    }


    protected JButton createButton(String caption, ActionListener listener) {
        JButton btn = new JButton(caption);
        btn.setActionCommand(caption);
        btn.setPreferredSize(new Dimension(90, 25));
        btn.addActionListener(listener);
        return btn;
    }


    private boolean isWorkletTask(AtomicTask task) {
        if (task == null) return false;
        YAWLServiceGateway decomposition =
                (YAWLServiceGateway) task.getDecomposition();
        if (decomposition != null) {
            YAWLServiceReference service = decomposition.getYawlService();
            if (service != null) {
                String uri = service.getServiceID();
                return uri != null && uri.contains("workletService/ib");
            }
        }
        return false;
    }


    private void enabledTaskCombo(RuleType ruleType) {
        _cbxTask.setEnabled(ruleType.isItemLevelType());
        _cbxTaskPrompt.setEnabled(ruleType.isItemLevelType());
    }

    private java.util.List<VariableRow> getDataContext(AtomicTask task) {
        java.util.List<VariableRow> rows = new ArrayList<VariableRow>();
        YDecomposition decomposition;
        if (task == null) {       // case level
            decomposition = SpecificationModel.getNets().getRootNet().getDecomposition();
            if (decomposition != null) {
                for (YVariable local : ((YNet) decomposition).getLocalVariables().values()) {
                    rows.add(new VariableRow(local, false, decomposition.getID()));
                }
            }
        }
        else {
            decomposition = task.getDecomposition();
        }

        if (decomposition != null) {
            String id = task != null ? task.getID() : decomposition.getID();
            for (YParameter input : decomposition.getInputParameters().values()) {
                rows.add(new VariableRow(input, false, id));
            }
        }

        Collections.sort(rows);
        return rows;
    }


    protected Element getDataElement() {
        YSpecificationID specID = SpecificationModel.getHandler()
                .getSpecification().getSpecificationID();
        RuleType rule = (RuleType) _cbxType.getSelectedItem();
        String taskID = rule.isItemLevelType() ?
                ((AtomicTask) _cbxTask.getSelectedItem()).getID() : null;
        return getDataElement(specID, rule, taskID);
    }


    protected Element getDataElement(YSpecificationID specID, RuleType rule, String taskID) {
        String dataRootName = rule.isCaseLevelType() ? specID.getUri() : taskID;
        return getDataElement(dataRootName);
    }


    protected Element getDataElement(String dataRootName) {
        return _dataContextPanel.getDataElement(dataRootName);
    }


    private void updateCondition(VariableRow row) {
        if (row != null) {
            String condition = row.getName();
            String value = row.getValue();
            String dataType = row.getDataType();
            if (value == null) {
                if (XSDType.isNumericType(dataType)) value = "0";
                else if (XSDType.isBooleanType(dataType)) value = "false";
                else value = "";
            }
            if (dataType.equals("string")) {
                value = "\"" + value + "\"";
            }
            condition += " = " + value;

            _txtCondition.setText(condition);
            _txtCondition.getInputVerifier().verify(_txtCondition);

            if (row.isValidValue()) {
                updateStatus("Variable '" + row.getName() + "' has valid value.");
            }
            else {
                updateStatus("Variable '" + row.getName() +
                        "' has invalid value for value type.");
            }
        }
    }


    private void validateConclusion() {
        java.util.List<ExletValidationError> errors = new ExletValidator().validate(
                _conclusionPanel.getConclusion(), getWorkletList());
        updateStatus("==== Action Set Validation ====");
        if (errors.isEmpty()) {
            updateStatus("OK");
        }
        else for (ExletValidationError error : errors) {
            updateStatus(error.getMessage());
        }
        _conclusionPanel.setVisuals(errors);
    }


    private Set<String> getWorkletList() {
        Set<String> workletNames = new HashSet<String>();
        try {
            for (YSpecificationID specID : new WorkletClient().getWorkletIdList()) {
                 workletNames.add(specID.getUri());
            }
        }
        catch (IOException ioe) {
            // fallthrough
        }
        return  workletNames;
    }


    private void addRule() {
        YSpecificationID specID = SpecificationModel.getHandler()
                .getSpecification().getSpecificationID();
        RuleType rule = (RuleType) _cbxType.getSelectedItem();
        String taskID = rule.isItemLevelType() ?
                ((AtomicTask) _cbxTask.getSelectedItem()).getID() : null;
        RdrNode node = new RdrNode(_txtCondition.getText(),
                _conclusionPanel.getConclusion(),
                getDataElement(specID, rule, taskID));

        WorkletClient client = new WorkletClient();
        try {
            String result = client.addRule(specID, taskID, rule, node);
            if (client.successful(result)) {
                reportSuccess("Rule successfully added.");
            }
            else reportError(StringUtil.unwrap(result));
        }
        catch (IOException ioe) {
            reportError(ioe.getMessage());
        }

        // also add service to task for worklet selection
        if (rule.isItemLevelType()) {
            YAWLServiceReference service = getServiceReference();
            if (service != null) {
                AtomicTask task = (AtomicTask) _cbxTask.getSelectedItem();
                YDecomposition decomposition = getOrCreateDecomposition(task);
                if (decomposition != null) {
                    ((YAWLServiceGateway) decomposition).setYawlService(service);
                }
            }
        }
        clearInputs();
    }


    private void clearInputs() {
        _txtCondition.setText(null);
        _txtDescription.setText(null);
        _conclusionPanel.setConclusion(null);
        _txtStatus.setText(null);
        _btnAdd.setEnabled(false);
        _btnClose.setEnabled(false);
    }


    private YAWLServiceReference getServiceReference() {
        for (YAWLServiceReference service : YConnector.getServices()) {
            String uri = service.getURI();
            if (uri != null && uri.contains("workletService")) {
                return service;
            }
        }
        return null;
    }


    private YDecomposition getOrCreateDecomposition(AtomicTask task) {
        YDecomposition decomposition = task.getDecomposition();
        if (decomposition == null) {
            try {
                decomposition = SpecificationModel.getHandler()
                        .getControlFlowHandler().addTaskDecomposition(task.getName());
                task.setDecomposition(decomposition);
            }
            catch (YControlFlowHandlerException ycfhe) {
                //
            }
        }
        return decomposition;
    }


    private void reportError(String msg) {
        updateStatus("========== ERROR ==========");
        updateStatus(msg);
        updateStatus("===========================");
    }


    private void reportSuccess(String msg) {
        updateStatus("========= SUCCESS =========");
        updateStatus(msg);
        updateStatus("===========================");
    }



    public void updateStatus(String msg) {
        String currentText = _txtStatus.getText();
        if (! currentText.isEmpty()) currentText += '\n';
        _txtStatus.setText(currentText + msg);
    }


}
