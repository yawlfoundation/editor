package org.yawlfoundation.yawl.worklet.dialog;

import org.jdom2.Element;
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
import org.yawlfoundation.yawl.worklet.rdr.RuleType;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.Vector;

/**
 * @author Michael Adams
 * @date 3/12/2015
 */
public class NodePanel extends JPanel implements EventListener {

    private DataContextTablePanel _dataContextPanel;
    private JComboBox _cbxType;
    private JComboBox _cbxTask;
    private JLabel _cbxTaskPrompt;
    private JTextField _txtCondition;
    private JTextArea _txtDescription;
    private ConclusionTablePanel _conclusionPanel;

    public NodePanel(AtomicTask task, CellEditorListener cellEditorListener,
                     ItemListener itemListener, AddRuleDialog ruleDialog) {
        super();
        addContent(task, cellEditorListener, itemListener, ruleDialog);
    }





    private void addContent(AtomicTask task, CellEditorListener cellEditorListener,
                            ItemListener itemListener, AddRuleDialog ruleDialog) {
        setLayout(new BorderLayout());
        add(getActionPanel(task, cellEditorListener, itemListener, ruleDialog), BorderLayout.WEST);
        add(getDataPanel(task), BorderLayout.CENTER);
    }


    private JPanel getActionPanel(AtomicTask task, CellEditorListener cellEditorListener,
                                  ItemListener itemListener, AddRuleDialog ruleDialog) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getRulePanel(task, itemListener, ruleDialog), BorderLayout.NORTH);
        panel.add(getDescriptionPanel(), BorderLayout.SOUTH);
        panel.add(getConclusionPanel(cellEditorListener), BorderLayout.CENTER);
        return panel;
    }


    private JPanel getDataPanel(AtomicTask task) {
        _dataContextPanel = new DataContextTablePanel(this);
        _dataContextPanel.setVariables(getDataContext(task));
        return _dataContextPanel;
    }


    private JPanel getRulePanel(AtomicTask task, ItemListener listener,
                                AddRuleDialog ruleDialog) {
        JPanel panel = new JPanel(new SpringLayout());
        _cbxTask = getTaskCombo(task, listener);
        _cbxType = getTypeCombo(listener);
        addContent(panel, "Rule Type:", _cbxType);
        _cbxTaskPrompt = addContent(panel, "Task:", _cbxTask);
        _txtCondition = getConditionField(ruleDialog);
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
        combo.addItemListener(listener);
        combo.setEnabled(false);                    // initially pre-case rule selected
        return combo;
    }


    private JTextField getConditionField(AddRuleDialog ruleDialog) {
        final JTextField field = new JTextField();
        field.setInputVerifier(new ConditionVerifier(ruleDialog));
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


    private JPanel getConclusionPanel(CellEditorListener listener) {
        _conclusionPanel = new ConclusionTablePanel(_cbxType, listener);
        return _conclusionPanel;
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










}
