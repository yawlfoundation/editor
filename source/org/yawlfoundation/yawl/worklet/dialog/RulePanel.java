package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.elements.model.AtomicTask;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
import org.yawlfoundation.yawl.editor.ui.net.NetGraphModel;
import org.yawlfoundation.yawl.editor.ui.net.utilities.NetUtilities;
import org.yawlfoundation.yawl.editor.ui.properties.data.VariableRow;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.elements.YAWLServiceGateway;
import org.yawlfoundation.yawl.elements.YAWLServiceReference;
import org.yawlfoundation.yawl.schema.XSDType;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * @author Michael Adams
 * @date 4/12/2015
 */
public class RulePanel extends JPanel {

    private JComboBox _cbxType;
    private JComboBox _cbxTask;
    private JLabel _cbxTaskPrompt;
    private ConditionPanel _conditionPanel;


    public RulePanel(AtomicTask task, NodePanel parent) {
        super();
        setContent(task, parent);
    }


    public void enabledTaskCombo(RuleType ruleType) {
        _cbxTask.setEnabled(ruleType.isItemLevelType());
        _cbxTaskPrompt.setEnabled(ruleType.isItemLevelType());
    }


    protected void clearInputs() { _conditionPanel.clearInputs(); }


    public boolean hasValidContent() { return _conditionPanel.hasValidContent(); }


    public String getCondition() { return _conditionPanel.getCondition(); }


    public RuleType getSelectedRule() { return (RuleType) _cbxType.getSelectedItem(); }


     public AtomicTask getSelectedTask() {
         return getSelectedRule().isItemLevelType() ?
                 (AtomicTask) _cbxTask.getSelectedItem() : null;
     }


    public void updateCondition(VariableRow row) {
        if (row != null) {
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

            _conditionPanel.setCondition(row.getName() + " = " + value);
        }
    }


    private void setContent(AtomicTask task, NodePanel parent) {
        setLayout(new SpringLayout());
        _cbxTask = getTaskCombo(task, parent);
        _cbxType = getTypeCombo(parent);
        addPrompt("Rule Type:", _cbxType);
        _cbxTaskPrompt = addPrompt("Task:", _cbxTask);
        _conditionPanel = new ConditionPanel(parent);
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



    private JComboBox getTypeCombo(ItemListener listener) {
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

        combo.addItemListener(listener);
        return combo;
    }


    private JComboBox getTaskCombo(AtomicTask task, ItemListener listener) {
        JComboBox combo = new JComboBox(getTaskList());

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
        combo.addItemListener(listener);
        combo.setEnabled(false);                    // initially pre-case rule selected
        return combo;
    }


    private Vector<AtomicTask> getTaskList() {
        Vector<AtomicTask> taskVector = new Vector<AtomicTask>();
        for (NetGraphModel model : SpecificationModel.getNets()) {
            for (YAWLAtomicTask netTask : NetUtilities.getAtomicTasks(model)) {
                 if (netTask instanceof AtomicTask) {
                     taskVector.add((AtomicTask) netTask);
                 }
            }
        }

        Collections.sort(taskVector, new Comparator<AtomicTask>() {
            @Override
            public int compare(AtomicTask t1, AtomicTask t2) {
                if (t1 == null) return -1;
                if (t2 == null) return 1;
                return t1.getID().compareTo(t2.getID());
            }
        });

        return taskVector;

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


}
