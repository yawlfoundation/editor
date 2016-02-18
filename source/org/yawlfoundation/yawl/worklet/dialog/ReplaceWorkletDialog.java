package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.util.SplitPaneUtil;
import org.yawlfoundation.yawl.elements.data.YVariable;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.SpecificationData;
import org.yawlfoundation.yawl.engine.interfce.TaskInformation;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;
import org.yawlfoundation.yawl.worklet.selection.WorkletRunner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collections;

/**
 * @author Michael Adams
 * @date 11/12/2015
 */
public class ReplaceWorkletDialog extends AbstractNodeDialog
        implements ActionListener, ListSelectionListener {

    private NodePanel _nodePanel;
    private RunnerTablePanel _runnerPanel;
    private JButton _btnAdd;
    private JButton _btnClose;
    private static final WorkletClient CLIENT = WorkletClient.getInstance();

    // both these values must be different for a valid replace
    private String _origCondition;
    private String _origConclusion;


    public ReplaceWorkletDialog(java.util.List<WorkletRunner> runners) {
        super(YAWLEditor.getInstance());
        setTitle("Replace Worklet");
        setContent(runners);
        setBaseSize(800, 700);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    @Override
    public void enableButtons() {
        boolean enable = false;

        if (_nodePanel.hasValidContent()) {
            RdrNode newNode = _nodePanel.getRdrNode();

            // ensure new node is  different from original
            enable = !(newNode == null || equalCondition(newNode) || equalConclusion(newNode));
        }

        _btnAdd.setEnabled(enable);
        _btnClose.setEnabled(enable);
    }


    @Override
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();
        if (cmd.equals("Cancel")) {
            close();
        }
        else if (cmd.equals("Reset")) {
            reset();
        }
        else if (cmd.equals("Replace Worklet")) {
            replace();
        }
        else if (cmd.equals("Replace & Close")) {
            replace();
            close();
        }
    }


    @Override
    public void valueChanged(ListSelectionEvent e) {        // runner selected
        if (!e.getValueIsAdjusting()) {
            updateNodePanel(_runnerPanel.getSelection());
        }
    }


    private void close() {
        CLIENT.clearCache();
        setVisible(false);
    }


    private void reset() {
        updateNodePanel(_runnerPanel.getSelection());
    }


    private void setContent(java.util.List<WorkletRunner> runners) {
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        new SplitPaneUtil().setupDivider(splitPane, false);
        splitPane.setBorder(new EmptyBorder(8, 8, 8, 8));
        splitPane.setTopComponent(getRunnerPanel(runners));
        splitPane.setBottomComponent(getNodePanel());
        add(splitPane, BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.SOUTH);
    }


    private JPanel getRunnerPanel(java.util.List<WorkletRunner> runners) {
        _runnerPanel = new RunnerTablePanel(this);
        _runnerPanel.setRows(runners);
        return _runnerPanel;
    }

    private JPanel getNodePanel() {
        _nodePanel = new NodePanel(null, this, DialogMode.Replacing);
        _nodePanel.setConditionStatus(null);
        return _nodePanel;
    }


    private JPanel getButtonPanel() {
        ButtonPanel panel = new ButtonPanel();
        panel.setBorder(new EmptyBorder(5,5,10,5));
        panel.addButton("Cancel", this);
        panel.addButton("Reset", this);
        _btnAdd = panel.addButton("Replace Worklet", this);
        _btnClose = panel.addButton("Replace & Close", this);
        _btnAdd.setEnabled(false);
        _btnClose.setEnabled(false);
        panel.equalise();
        return panel;
    }


    private boolean equalCondition(RdrNode node) {
        if (_origCondition.equals(node.getCondition())) {
            _nodePanel.setConditionStatus("Different condition required");
            return true;
        }
        return false;
    }


    private boolean equalConclusion(RdrNode node) {
        if (_origConclusion.equals(node.getConclusion().toString())) {
            _nodePanel.setConclusionStatus("Different action set required");
            return true;
        }
        return false;
    }


    private void clearInputs() {
        _btnAdd.setEnabled(false);
        _btnClose.setEnabled(false);
        _nodePanel.clearInputs();
    }


    private void replace() {
        WorkletRunner runner = _runnerPanel.getSelection();
        if (addRule(runner)) {
            try {
                String caseID = reprocessSelection(runner);
                MessageDialog.info("Worklet successfully replaced (case: " + caseID + ")",
                        "Replace Worklet");
                clearInputs();
                _runnerPanel.setRows(refreshRunners());

            }
            catch (IOException ioe) {
                MessageDialog.error(ioe.getMessage(), "Replace Worklet");
            }
        }
    }


    private void updateNodePanel(WorkletRunner runner) {
        if (runner == null) return;

        java.util.List<? extends YVariable> varList;
        String id;
        WorkItemRecord wir = runner.getWir();
        try {
            if (wir != null) {
                TaskInformation taskInfo = CLIENT.getTaskInfo(
                        new YSpecificationID(wir), wir.getTaskID());
                varList = taskInfo.getParamSchema().getInputParams();
                id = taskInfo.getDecompositionID();
            }
            else {
                YSpecificationID specID = runner.getParentSpecID();
                SpecificationData specInfo = CLIENT.getSpecificationInfo(specID);
                varList = specInfo.getInputParams();
                id = specInfo.getRootNetID();
            }
            _nodePanel.setNode(varList, id, runner, getRdrNode(runner.getRuleNodeID()));
            _nodePanel.setConditionStatus("Different condition required");
            _nodePanel.setConclusionStatus("Different action set required");
        }
        catch (IOException ioe) {
            MessageDialog.error("Unable to get worklet instance detail from service",
                    "Service Error");
        }
    }


    private RdrNode getRdrNode(long id) throws IOException {
        RdrNode node = CLIENT.getRdrNode(id);
        if (node != null) {
            _origCondition = node.getCondition();
            _origConclusion = node.getConclusion().toString();
            try {
                return node.clone();      // clone it so values can be changed
            }
            catch (CloneNotSupportedException cnse) {
                throw new IOException("Failed to get cloned node");
            }
        }
        return null;
    }


    private boolean addRule(WorkletRunner runner) {
        YSpecificationID specID = runner.getParentSpecID();
        String taskID = runner.getTaskID();
        RuleType rule = runner.getRuleType();
        RdrNode node = _nodePanel.getRdrNode();

        try {
            CLIENT.addRule(specID, taskID, rule, node);
            return true;
        }
        catch (IOException ioe) {
            MessageDialog.error(ioe.getMessage(), "Replace worklet rule");
        }
        return false;
    }


    private String reprocessSelection(WorkletRunner runner) throws IOException {
        RuleType ruleType = runner.getRuleType();
        try {
            if (ruleType == RuleType.ItemSelection) {
                return CLIENT.replaceWorklet(runner.getWorkItemID());
            }
            else {
                return CLIENT.replaceWorklet(runner.getParentCaseID(),
                        runner.getWorkItemID(), ruleType);
            }
        }
        catch (IOException ioe) {
            MessageDialog.error(ioe.getMessage(), "Replace worklet");
        }
        return null;
    }


    private java.util.List<WorkletRunner> refreshRunners() {
        try {
            return CLIENT.getRunningWorkletList();
        }
        catch (IOException ioe) {
            return Collections.emptyList();
        }
    }

}
