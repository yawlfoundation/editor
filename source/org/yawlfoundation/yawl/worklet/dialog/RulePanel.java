package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.ui.properties.data.VariableRow;
import org.yawlfoundation.yawl.elements.YAWLServiceGateway;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;
import org.yawlfoundation.yawl.worklet.selection.WorkletRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 4/12/2015
 */
public class RulePanel extends JPanel implements ItemListener {

    private JComboBox _cbxType;
    private TaskComboBox _cbxTask;
    private JLabel _cbxTaskPrompt;
    private ConditionPanel _conditionPanel;
    private DialogMode _mode;


    public RulePanel(YAWLAtomicTask task, NodePanel parent, DialogMode mode) {
        super();
        _mode = mode;
        setContent(task, parent, mode);
    }


    @Override
    public void itemStateChanged(ItemEvent e) {            // rule type selection
        if (e.getStateChange() == ItemEvent.SELECTED) {
            RuleType selectedType = (RuleType) e.getItem();

            if (_mode != DialogMode.Viewing) {
                if (selectedType.isCaseLevelType()) {
                    _cbxTask.setItem(null);              // clear and disable task combo
                }
                else {
                    _cbxTask.setItems();                 // load and enable task combo
                }
            }
            _cbxTaskPrompt.setEnabled(_cbxTask.isEnabled());
        }
    }


    protected void clearInputs() { _conditionPanel.clearInputs(); }


    public boolean hasValidContent() { return _conditionPanel.hasValidContent(); }


    public String getCondition() { return _conditionPanel.getCondition(); }


    public RuleType getSelectedRule() { return (RuleType) _cbxType.getSelectedItem(); }

    public void setSelectedRule(int index) { _cbxType.setSelectedIndex(index); }


     public YAWLAtomicTask getSelectedTask() {
         return getSelectedRule().isItemLevelType() ? _cbxTask.getSelectedTask() : null;
     }

    public void setSelectedTask(int index) { _cbxTask.setSelectedIndex(index); }


    public void updateCondition(VariableRow row) {
        _conditionPanel.updateCondition(row);
    }


    // from replace dialog
    public void setNode(WorkletRunner runner, RdrNode ruleNode) {
        _cbxType.setEnabled(false);
        _cbxTask.setEnabled(false);
        _cbxTaskPrompt.setEnabled(false);
        _cbxType.setSelectedItem(runner.getRuleType());
        _cbxTask.setItem(runner.getTaskID());
        _conditionPanel.setCondition(ruleNode.getCondition());
    }

    // from view dialog
    public void setNode(RdrNode ruleNode) {
        _conditionPanel.setCondition(ruleNode.getCondition());
    }


    public void setConditionStatus(String status) {
        _conditionPanel.setStatus(status);
    }


    public void setRuleComboItems(Set<RuleType> items) {
        _cbxType.removeAllItems();
        java.util.List<RuleType> sortedItems = new ArrayList<RuleType>(items);
        Collections.sort(sortedItems);
        for (RuleType item : sortedItems) { _cbxType.addItem(item); };
    }

    public void setTaskComboItems(Set<String> items) {
        _cbxTask.setItems(items);
    }


    private void setContent(YAWLAtomicTask task, NodePanel parent, DialogMode mode) {
        setLayout(new SpringLayout());
        _cbxTask = getTaskCombo(task, parent);
        _cbxType = getTypeCombo(parent);
        addPrompt("Rule Type:", _cbxType).setEnabled(mode != DialogMode.Replacing);
        _cbxTaskPrompt = addPrompt("Task:", _cbxTask);
        _cbxTaskPrompt.setEnabled(false);
        _conditionPanel = new ConditionPanel(parent, mode);
        addPrompt(_conditionPanel.getPrompt(), _conditionPanel);
        SpringUtil.makeCompactGrid(this, 3, 2, 6, 6, 8, 8);

        if (isWorkletTask(task)) {
            _cbxType.setSelectedItem(RuleType.ItemSelection);
        }
    }

    private JLabel addPrompt(String prompt, Component c) {
        return addPrompt(new JLabel(prompt, JLabel.LEADING), c);
    }


    private JLabel addPrompt(JLabel label, Component c) {
        label.setFont((Font) UIManager.get("TitledBorder.font"));
        label.setForeground((Color) UIManager.get("TitledBorder.titleColor"));
        add(label);
        label.setLabelFor(c);
        add(c);
        return label;
    }


    private JComboBox getTypeCombo(NodePanel parent) {
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

        if (parent.getDialog().isComboListener()) {
            combo.addItemListener(this);
            combo.addItemListener(parent);
        }
        else {
            combo.setEnabled(false);
        }
        return combo;
    }


    private TaskComboBox getTaskCombo(YAWLAtomicTask task, NodePanel parent) {
        TaskComboBox combo = new TaskComboBox(task);
        if (parent.getDialog().isComboListener()) {       // add & view dialogs listen
            combo.addItemListener(parent);
            combo.listenForSelections();
        }
        return combo;
    }


    private boolean isWorkletTask(YAWLAtomicTask task) {
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

}
