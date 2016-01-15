package org.yawlfoundation.yawl.worklet.dialog;

import org.jdom2.Element;
import org.yawlfoundation.yawl.editor.ui.elements.model.AtomicTask;
import org.yawlfoundation.yawl.editor.ui.properties.data.VariableRow;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.util.SplitPaneUtil;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.data.YVariable;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.util.XNode;
import org.yawlfoundation.yawl.util.XNodeParser;
import org.yawlfoundation.yawl.worklet.rdr.RdrConclusion;
import org.yawlfoundation.yawl.worklet.rdr.RdrNode;
import org.yawlfoundation.yawl.worklet.rdr.RuleType;
import org.yawlfoundation.yawl.worklet.selection.WorkletRunner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;

/**
 * @author Michael Adams
 * @date 3/12/2015
 */
public class NodePanel extends JPanel implements EventListener, ItemListener,
        CellEditorListener, ListSelectionListener {

    private DataContextTablePanel _dataContextPanel;
    private RulePanel _rulePanel;
    private JTextArea _txtDescription;
    private ConclusionTablePanel _conclusionPanel;
    private AbstractNodeDialog _parent;
    private DialogMode _mode;


    public NodePanel(AtomicTask task, AbstractNodeDialog parent, DialogMode mode) {
        super();
        _parent = parent;
        setContent(task);
        setMode(mode);
    }


    public void addConclusionTableCellEditorListener(CellEditorListener listener) {
        _conclusionPanel.getTable().addCellEditorListener(listener);
    }


    public void addDataContextEventListener(EventListener listener) {
        _dataContextPanel.addEventListener(listener);
    }


    // combo selection
    public void itemStateChanged(ItemEvent event) {
        if (((JComboBox) event.getSource()).isEnabled() &&
                event.getStateChange() == ItemEvent.SELECTED) {
            if (_mode == DialogMode.Viewing) {
                _parent.comboChanged(event);
            }
            else {
                java.util.List<VariableRow> variables = null;
                Object item = event.getItem();

                // if rule change
                if (item instanceof RuleType) {
                    RuleType selectedType = (RuleType) item;

                    if (selectedType.isCaseLevelType()) {
                        variables = getDataContext(null);  // net level vars
                    }
                    else {
                        AtomicTask task = getSelectedTask();
                        if (task != null) {
                            variables = getDataContext(task);
                        }
                    }
                }
                else {    // task combo
                    variables = getDataContext((AtomicTask) item);
                }
                if (variables == null) variables = Collections.emptyList();

                _dataContextPanel.setVariables(variables);
                clearInputs();
            }
        }
    }

    // data table selection
    public void valueChanged(ListSelectionEvent event) {
        if (! event.getValueIsAdjusting()) {
            _rulePanel.updateCondition(_dataContextPanel.getSelectedVariable());
        }
    }


    public void conclusionEditingStarted() {
        _conclusionPanel.enableButtons(false);
    }

    // conclusion & data tables value edit
    public void editingStopped(ChangeEvent e) {
        if (e.getSource() instanceof ExletCellEditor) {
            validateConclusion();
            _conclusionPanel.enableButtons(true);
        }
        else {
            _rulePanel.updateCondition(_dataContextPanel.getSelectedVariable());
        }
    }


    // data table value edit cancel
    public void editingCanceled(ChangeEvent e) { }


    public AbstractNodeDialog getDialog() { return _parent; }


    public boolean hasValidContent() {
        return _dataContextPanel.hasValidContent() && _conclusionPanel.hasValidContent() &&
                _rulePanel.hasValidContent();
    }


    public RdrNode getRdrNode() {
        String condition = _rulePanel.getCondition();
        RdrConclusion conclusion = _conclusionPanel.getConclusion();
        Element cornerstone = _dataContextPanel.getDataElement("cornerstone");

        if (! (condition == null || (conclusion == null || conclusion.isNullConclusion())
                || cornerstone == null)) {
            RdrNode node = new RdrNode(condition, conclusion, cornerstone);
            node.setDescription(_txtDescription.getText());
            return node;
        }
        return null;
    }


    public RuleType getSelectedRule() { return _rulePanel.getSelectedRule(); }


    public AtomicTask getSelectedTask() {
        return _rulePanel != null ? _rulePanel.getSelectedTask() : null; }


    public void setNode(java.util.List<? extends YVariable> vars, String id,
                        WorkletRunner runner, RdrNode ruleNode) {
        _dataContextPanel.setNode(getDataRows(vars, id, runner),
                getCornerstoneNode(ruleNode));
        _conclusionPanel.setNode(ruleNode);
        _rulePanel.setNode(runner, ruleNode);
        _txtDescription.setText(ruleNode.getDescription());
    }


    public void setNode(RdrNode ruleNode) {
        _dataContextPanel.setNode(getDataContext(getSelectedTask()),
                getCornerstoneNode(ruleNode));
        _conclusionPanel.setNode(ruleNode);
        _rulePanel.setNode(ruleNode);
        _txtDescription.setText(ruleNode.getDescription());
    }


    public void setConditionStatus(String status) {
        _rulePanel.setConditionStatus(status);
    }

    public void setConclusionStatus(String status) {
        _conclusionPanel.setStatus(status);
    }


    private void setMode(DialogMode mode) {
        _mode = mode;
        _dataContextPanel.setMode(mode);
        _conclusionPanel.setMode(mode);
        _rulePanel.setMode(mode);
        _txtDescription.setEditable(mode != DialogMode.Viewing);
        _txtDescription.setBackground(Color.WHITE);
    }


    private void setContent(AtomicTask task) {
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        new SplitPaneUtil().setupDivider(splitPane, false);
        splitPane.setRightComponent(getDataPanel(task));        // init data panel first
        splitPane.setLeftComponent(getActionPanel(task));
        add(splitPane, BorderLayout.CENTER);
    }


    private JPanel getActionPanel(AtomicTask task) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getRulePanel(task), BorderLayout.NORTH);
        panel.add(getDescriptionPanel(), BorderLayout.SOUTH);
        panel.add(getConclusionPanel(), BorderLayout.CENTER);
        return panel;
    }


    private JPanel getDataPanel(AtomicTask task) {
        _dataContextPanel = new DataContextTablePanel(this);
        _dataContextPanel.setVariables(getDataContext(task));
        return _dataContextPanel;
    }


    private JPanel getRulePanel(AtomicTask task) {
        _rulePanel = new RulePanel(task, this);
        return _rulePanel;
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
        _conclusionPanel = new ConclusionTablePanel(this);
        return _conclusionPanel;
    }


     private java.util.List<VariableRow> getDataContext(AtomicTask task) {
         java.util.List<VariableRow> rows = new ArrayList<VariableRow>();
         YDecomposition decomposition;
         if (task == null) {       // case level
             decomposition = SpecificationModel.getNets().getRootNet().getDecomposition();
             if (decomposition != null) {
                 rows = getDataRows(((YNet) decomposition).getLocalVariables().values(),
                         decomposition.getID());
             }
         }
         else {
             decomposition = task.getDecomposition();
         }

         if (decomposition != null) {
             String id = task != null ? task.getID() : decomposition.getID();
             rows = getDataRows(decomposition.getInputParameters().values(), id);
         }

         Collections.sort(rows);
         return rows;
     }


    private java.util.List<VariableRow> getDataRows(Collection<? extends YVariable> variables,
                                                    String rootName) {
        return getDataRows(variables, rootName, null);
    }


    private java.util.List<VariableRow> getDataRows(Collection<? extends YVariable> variables,
                                                    String rootName, WorkletRunner runner) {
        java.util.List<VariableRow> rows = new ArrayList<VariableRow>();
        for (YVariable variable : variables) {
            rows.add(new VariableRow(variable, false, rootName));
        }
        if (! (rows.isEmpty() || runner == null)) {
            XNodeParser parser = new XNodeParser();
            XNode dataNode = parser.parse(runner.getDataListString());
            if (dataNode != null) {
                for (VariableRow row : rows) {
                    String name = row.getName();
                    XNode varNode = dataNode.getChild(name);
                    if (varNode != null) {
                        row.setValue(varNode.hasChildren() ? varNode.toPrettyString() :
                                varNode.getText());
                    }
                }
            }
        }
        return rows;
    }


    private XNode getCornerstoneNode(RdrNode ruleNode) {
        return new XNodeParser().parse(ruleNode.getCornerStone());
    }


    protected void clearInputs() {
        _txtDescription.setText(null);
        _rulePanel.clearInputs();
        _conclusionPanel.setConclusion(null);
    }


    protected Element getDataElement() {
        YSpecificationID specID = SpecificationModel.getHandler()
                .getSpecification().getSpecificationID();
        RuleType rule = getSelectedRule();
        AtomicTask task = getSelectedTask();
        String taskID = task != null ? task.getID() : null;
        return getDataElement(specID, rule, taskID);
    }


     protected Element getDataElement(YSpecificationID specID, RuleType rule, String taskID) {
         String dataRootName = rule.isCaseLevelType() ? specID.getUri() : taskID;
         return _dataContextPanel.getDataElement(dataRootName);
     }


    private void validateConclusion() {
        _conclusionPanel.validateConclusion();
    }


}
