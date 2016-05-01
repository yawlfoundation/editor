package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.util.SplitPaneUtil;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.rdr.*;
import org.yawlfoundation.yawl.worklet.rdrutil.RdrResult;
import org.yawlfoundation.yawl.worklet.tree.TreePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 11/12/2015
 */
public class ViewTreeDialog extends AbstractNodeDialog
        implements ActionListener {

    private NodePanel _nodePanel;
    private TreePanel _treePanel;
    private JScrollPane _treeScrollPane;
    private CompositeRulePanel _compositeRulePanel;
    private RdrSet _rdrSet;
    private RdrNode _selectedNode;


    public ViewTreeDialog(RdrSet rdrSet) {
        super(YAWLEditor.getInstance(), true);
        setTitle("Rule Browser");
        _rdrSet = rdrSet;
        setContent();
        setBaseSize(800, 550);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
    }


    @Override
    public void enableButtons() { }


    @Override
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();
        if (cmd.equals("Remove")) {
            removeRule();
        }
        if (cmd.equals("Done")) {
            close();
        }
    }


    // from TreePanel
    public void nodeSelected(RdrNode ruleNode) {
        _selectedNode = ruleNode;
        if (ruleNode != null) {
            _nodePanel.setNode(ruleNode);
            _compositeRulePanel.setCondition(ruleNode);
        }
    }


    public void comboChanged(ItemEvent event) {
        Object item = event.getItem();
        RuleType selectedType;
        if (item instanceof RuleType) {
            selectedType = (RuleType) item;
            _nodePanel.setTaskComboItems(getTasksForRule(selectedType));
        }
        else {
            selectedType = _nodePanel.getSelectedRule();
        }

        setTree(getTree(selectedType, getSelectedTaskID()), null);
    }


    private void close() {
        setVisible(false);
    }


    private RdrTree getTree(RuleType ruleType, String taskID) {
        taskID = WorkletClient.getInstance().getOldTaskID(taskID);
        return _rdrSet != null ? _rdrSet.getTree(ruleType, taskID) : null;
    }


    private void removeRule() {
        if (_selectedNode != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Remove selected rule - are you sure?",
                    "Remove Rule Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.NO_OPTION) {
                return;
            }
            RuleType rType = _nodePanel.getSelectedRule();
            String taskID = getSelectedTaskID();
            RdrTree tree = getTree(rType, taskID);
            if (tree != null) {
                RdrNode parent = _selectedNode.getParent();
                RdrNode node = tree.removeNode(_selectedNode.getNodeId());
                if (node != null) {
                    try {
                        RdrResult result = WorkletClient.getInstance().removeRule(
                                SpecificationModel.getHandler().getID(), taskID,
                                rType, node);
                        switch (result) {
                            case RdrSetRemoved: // no rules left in set
                                MessageDialog.info("The rule set is now empty. " +
                                        "This dialog will close.", "Remove Rule Success");
                                close();
                                break;
                            case RdrTreeSetRemoved: // no more trees for rule
                                _rdrSet.removeTreeSet(rType);
                                MessageDialog.info("There are no more " +
                                        rType + " rules.", "Remove Rule Success");
                                _nodePanel.setSelectedRule(0);
                                break;
                            case RdrTreeRemoved:
                                _rdrSet.getTreeSet(rType).remove(taskID);
                                MessageDialog.info("There are no more " + rType +
                                                 " rules for task '" + taskID + "'.",
                                        "Remove Rule Success");
                                _nodePanel.setSelectedTask(0);
                                break;
                            case RdrNodeRemoved:
                                setTree(tree, parent); break;   // refresh the tree panel
                        }
                    }
                    catch (IOException ioe) {
                        MessageDialog.error("Failed to remove rule from Worklet Service: " +
                                ioe.getMessage(), "Remove Rule Error");
                    }
                }
                else {
                    MessageDialog.warn("The selected rule cannot be removed",
                            "Remove Rule Error" );
                }
            }
        }
    }


    private String getSelectedTaskID() {
        YAWLAtomicTask task = _nodePanel.getSelectedTask();
        return task != null ? task.getID() : null;
    }


    private void setTree(RdrTree tree, RdrNode newSelection) {
        if (_treePanel != null) {
            _treePanel.setTree(tree, newSelection);
            Rectangle rootNodeRect = _treePanel.getRootNodeRect();
            if (rootNodeRect != null) {
                _treeScrollPane.setViewportView(_treePanel);
                _treeScrollPane.getViewport().scrollRectToVisible(rootNodeRect);
            }
        }
    }


    private void setContent() {
        setLayout(new BorderLayout());

        _compositeRulePanel = new CompositeRulePanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        new SplitPaneUtil().setupDivider(splitPane, false);
        splitPane.setBorder(new EmptyBorder(8, 8, 8, 8));
        splitPane.setLeftComponent(getTreePanel());
        splitPane.setRightComponent(getNodePanel());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(_compositeRulePanel, BorderLayout.SOUTH);
        splitPane.setResizeWeight(0.3);

        add(panel, BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.SOUTH);
    }


    private JPanel getTreePanel() {
        _treePanel = new TreePanel(this);
        _treeScrollPane = new JScrollPane(_treePanel);
        _treeScrollPane.setPreferredSize(new Dimension(300, 300));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Rule Tree"));
        panel.add(_treeScrollPane, BorderLayout.CENTER);
        return panel;
    }


    private JPanel getNodePanel() {
        _nodePanel = new NodePanel(null, this, DialogMode.Viewing);
        _nodePanel.setRuleComboItems(_rdrSet.getRules());
        return _nodePanel;
    }


    private JPanel getButtonPanel() {
        ButtonPanel panel = new ButtonPanel();
        panel.setBorder(new EmptyBorder(5,5,10,5));
        panel.addButton("Remove", this);
        panel.addButton("Done", this);
        panel.equalise();
        return panel;
    }


    private Set<String> getTasksForRule(RuleType ruleType) {
        if (ruleType.isCaseLevelType()) return null;

        Set<String> tasks = new HashSet<String>();
        RdrTreeSet treeSet = _rdrSet.getTreeSet(ruleType);
        if (treeSet != null) {
            for (String taskID : treeSet.getAllTasks()) {
                tasks.add(WorkletClient.getInstance().getUpdatedTaskID(taskID));
            }
        }
        return tasks;
    }

}
