package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.elements.model.AtomicTask;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.util.SplitPaneUtil;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RdrSet;
import org.yawlfoundation.yawl.worklet.rdr.RdrTree;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;
import org.yawlfoundation.yawl.worklet.tree.TreePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.IOException;

/**
 * @author Michael Adams
 * @date 11/12/2015
 */
public class ViewTreeDialog extends AbstractNodeDialog
        implements ActionListener {

    private NodePanel _nodePanel;
    private TreePanel _treePanel;
    private CompositeRulePanel _compositeRulePanel;
    private RdrSet _rdrSet;


    public ViewTreeDialog() {
        super(YAWLEditor.getInstance(), true);
        setTitle("Rule Tree Viewer");
        _rdrSet = loadRdrSet();
        setContent();
        setBaseSize(800, 650);
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
        RuleType selectedType = _nodePanel.getSelectedRule();
        AtomicTask task = _nodePanel.getSelectedTask();
        String taskID = task != null ? task.getID() : null;
        _treePanel.setTree(getTree(selectedType, taskID));
    }


    private void close() {
        setVisible(false);
    }


    private RdrSet loadRdrSet() {
        try {
            YSpecificationID specID = new YSpecificationID("Casualty_Treatment");
            String s = WorkletClient.getInstance().getRdrSet(specID);
            if (s != null) {
                RdrSet rdrSet = new RdrSet(specID);
                rdrSet.fromXML(s);
                return rdrSet;
            }
        }
        catch (IOException ioe) {
            MessageDialog.error(this, "Unable to load rule set from worklet service: " +
                    ioe.getMessage(), "Rule Set Load Error");
        }
        return null;
    }


    private RdrTree getTree(RuleType ruleType, String taskID) {
        return _rdrSet != null ? _rdrSet.getTree(ruleType, taskID) : null;
    }


    private void setContent() {
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        new SplitPaneUtil().setupDivider(splitPane, false);
        splitPane.setBorder(new EmptyBorder(8, 8, 8, 8));
        splitPane.setLeftComponent(getTreePanel());
        splitPane.setRightComponent(getNodePanel());

        _compositeRulePanel = new CompositeRulePanel();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(_compositeRulePanel, BorderLayout.SOUTH);
        splitPane.setResizeWeight(0.3);

        add(panel, BorderLayout.CENTER);
        add(getButtonPanel(), BorderLayout.SOUTH);
    }


    private JPanel getTreePanel() {
        RdrTree tree = getTree(RuleType.ItemSelection, "3_Treat");

        _treePanel = new TreePanel(tree, this);
        JScrollPane scrollPane = new JScrollPane(_treePanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Rule Tree"));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }


    private JPanel getNodePanel() {
        _nodePanel = new NodePanel(null, this, DialogMode.Viewing);
        return _nodePanel;
    }


    private JPanel getButtonPanel() {
        ButtonPanel panel = new ButtonPanel();
        panel.setBorder(new EmptyBorder(5,5,10,5));
        JButton btnClose = panel.addButton("Done", this);
        btnClose.setPreferredSize(new Dimension(75,25));
        return panel;
    }

}
