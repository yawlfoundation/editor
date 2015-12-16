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
import org.yawlfoundation.yawl.worklet.selection.WorkletRunner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * @author Michael Adams
 * @date 11/12/2015
 */
public class ReplaceWorkletDialog extends AbstractNodeDialog
        implements ActionListener, ListSelectionListener {

    private NodePanel _nodePanel;
    private RunnerTablePanel _runnerPanel;


    public ReplaceWorkletDialog(java.util.List<WorkletRunner> runners) {
        super(YAWLEditor.getInstance());
        setTitle("Replace Worklet");
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContent(runners);
        setPreferredSize(new Dimension(800, 700));
        setMinimumSize(new Dimension(800, 700));
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    @Override
    public void enableButtons() {

    }


    @Override
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();
        if (cmd.equals("Cancel")) {
            // todo clear client cache
            setVisible(false);
        }
        else if (cmd.equals("Replace Worklet")) {
            replace();
        }
        else if (cmd.equals("Replace & Close")) {
            replace();
            // todo clear client cache
            setVisible(false);
        }
    }


    @Override
    public void valueChanged(ListSelectionEvent e) {        // runner selected
        if (!e.getValueIsAdjusting()) {
            updateNodePanel(_runnerPanel.getSelection());
        }
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
        _nodePanel = new NodePanel(null, this);
        return _nodePanel;
    }


    private JPanel getButtonPanel() {
        ButtonPanel panel = new ButtonPanel();
        panel.setBorder(new EmptyBorder(5,5,10,5));
        panel.addButton("Cancel", this);
        panel.addButton("Clear", this);
//        _btnAdd = panel.addButton("Add Rule", this);
//        _btnClose = panel.addButton("Add & Close", this);
//        _btnAdd.setEnabled(false);
//        _btnClose.setEnabled(false);
//        panel.add(_btnClose);
        return panel;
    }


    private void replace() { }


    private void updateNodePanel(WorkletRunner runner) {
        if (runner == null) return;

        java.util.List<? extends YVariable> varList;
        String id;
        WorkItemRecord wir = runner.getWir();
        try {
            if (wir != null) {
                TaskInformation taskInfo = WorkletClient.getInstance().getTaskInfo(
                        new YSpecificationID(wir), wir.getTaskID());
                varList = taskInfo.getParamSchema().getInputParams();
                id = taskInfo.getDecompositionID();
            }
            else {
                YSpecificationID specID = runner.getParentSpecID();
                SpecificationData specInfo = WorkletClient.getInstance()
                        .getSpecificationInfo(specID);
                varList = specInfo.getInputParams();
                id = specInfo.getRootNetID();
            }
            RdrNode node = WorkletClient.getInstance().getRdrNode(runner.getRuleNodeId());
            _nodePanel.setNode(varList, id, runner, node);
        }
        catch (IOException ioe) {
            MessageDialog.error("Unable to get worklet instance detail from service",
                    "Service Error");
        }
    }




}
