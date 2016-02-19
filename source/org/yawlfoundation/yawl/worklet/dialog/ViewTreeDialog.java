package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.elements.model.AtomicTask;
import org.yawlfoundation.yawl.editor.ui.util.SplitPaneUtil;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.rdr.*;
import org.yawlfoundation.yawl.worklet.tree.TreePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
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


    public ViewTreeDialog(RdrSet rdrSet) {
        super(YAWLEditor.getInstance(), true);
        setTitle("Rule Tree Viewer");
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
        if (cmd.equals("Done")) {
            close();
        }
    }


    // from TreePanel
    public void nodeSelected(RdrNode ruleNode) {
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

        AtomicTask task = _nodePanel.getSelectedTask();
        String taskID = task != null ? task.getID() : null;
        _treePanel.setTree(getTree(selectedType, taskID));
        _treeScrollPane.getViewport().scrollRectToVisible(_treePanel.getRootNodeRect());
    }


    private void close() {
        setVisible(false);
    }


    private RdrTree getTree(RuleType ruleType, String taskID) {
        taskID = WorkletClient.getInstance().getOldTaskID(taskID);
        return _rdrSet != null ? _rdrSet.getTree(ruleType, taskID) : null;
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
        JButton btnClose = panel.addButton("Done", this);
        btnClose.setPreferredSize(new Dimension(75,25));
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
