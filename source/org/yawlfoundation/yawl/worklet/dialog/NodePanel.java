package org.yawlfoundation.yawl.worklet.dialog;

import org.jdom2.Element;
import org.yawlfoundation.yawl.editor.ui.elements.model.YAWLAtomicTask;
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
import java.util.*;

import static org.yawlfoundation.yawl.worklet.dialog.DialogMode.Viewing;


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


    public NodePanel(YAWLAtomicTask task, AbstractNodeDialog parent, DialogMode mode) {
        super();
        _parent = parent;
        setContent(task, mode);
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
            if (_mode == Viewing) {
                _parent.comboChanged(event);
            }
            else {
                java.util.List<VariableRow> variables;
                Object item = event.getItem();

                // if rule change
                if (item instanceof RuleType) {
                    RuleType selectedType = (RuleType) item;

                    if (selectedType.isCaseLevelType()) {
                        variables = getDataContext(null);  // net level vars
                    }
                    else {
                        variables = getVariables(getSelectedTask());
                    }

                    if (_conclusionPanel != null) {
                        enableGraphicalRuleEditing(selectedType);
                    }
                }
                else {    // task combo
                    variables = getVariables((YAWLAtomicTask) item);
                }

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
            boolean valid = validateConclusion();
            _conclusionPanel.enableButtons(true);
            enableGraphicalRuleEditing(_mode, _rulePanel.getSelectedRule(), valid);
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

    public void setSelectedRule(int index) { }


    public YAWLAtomicTask getSelectedTask() {
        return _rulePanel != null ? _rulePanel.getSelectedTask() : null; }

    public void setSelectedTask(int index) { }


    public void setNode(java.util.List<? extends YVariable> vars, String id,
                        WorkletRunner runner, RdrNode ruleNode) {
        _dataContextPanel.setNode(getDataRows(vars, id, runner),
                getCornerstoneNode(ruleNode));
        _rulePanel.setNode(runner, ruleNode);
        _conclusionPanel.setNode(ruleNode);
        _txtDescription.setText(ruleNode.getDescription());
    }


    // from view dialog
    public void setNode(RdrNode ruleNode) {
        _dataContextPanel.setNode(getCornerstoneRows(ruleNode), null);
        _rulePanel.setNode(ruleNode);
        _conclusionPanel.setNode(ruleNode);
        _txtDescription.setText(ruleNode.getDescription());
    }


    public void setConditionStatus(String status) {
        _rulePanel.setConditionStatus(status);
    }

    public void setConclusionStatus(String status) {
        _conclusionPanel.setStatus(status);
    }


    public void setRuleComboItems(Set<RuleType> items) {
        _rulePanel.setRuleComboItems(items);
    }

    public void setTaskComboItems(Set<String> items) {
        _rulePanel.setTaskComboItems(items);
    }

    private void setMode(DialogMode mode) {
        _mode = mode;
        _txtDescription.setEditable(mode != DialogMode.Viewing);
        _txtDescription.setBackground(Color.WHITE);
    }


    private void setContent(YAWLAtomicTask task, DialogMode mode) {
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        new SplitPaneUtil().setupDivider(splitPane, false);
        splitPane.setRightComponent(getDataPanel(task, mode));  // init data panel first
        splitPane.setLeftComponent(getActionPanel(task, mode));
        add(splitPane, BorderLayout.CENTER);
        _dataContextPanel.setVariables(getVariables(getSelectedTask()));
    }


    private JPanel getActionPanel(YAWLAtomicTask task, DialogMode mode) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getDescriptionPanel(), BorderLayout.SOUTH);
        panel.add(getRulePanel(task, mode), BorderLayout.NORTH);
        panel.add(getConclusionPanel(mode), BorderLayout.CENTER);
        return panel;
    }


    private JPanel getDataPanel(YAWLAtomicTask task, DialogMode mode) {
        _dataContextPanel = new DataContextTablePanel(this, mode);
        _dataContextPanel.setVariables(getDataContext(task));
        return _dataContextPanel;
    }


    private JPanel getRulePanel(YAWLAtomicTask task, DialogMode mode) {
        _rulePanel = new RulePanel(task, this, mode);
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


    private JPanel getConclusionPanel(DialogMode mode) {
        _conclusionPanel = new ConclusionTablePanel(this, mode);
        enableGraphicalRuleEditing(mode, _rulePanel.getSelectedRule(), true);
        return _conclusionPanel;
    }


    private void enableGraphicalRuleEditing(RuleType selectedRule) {
        enableGraphicalRuleEditing(_mode, selectedRule, true);
    }


    private void enableGraphicalRuleEditing(DialogMode mode,
                                            RuleType selectedRule, boolean valid) {
        _conclusionPanel.enableGraphButton(valid && mode != Viewing &&
                selectedRule != RuleType.ItemSelection);
    }


    private java.util.List<VariableRow> getVariables(YAWLAtomicTask task) {
        return task != null ? getDataContext(task) : Collections.<VariableRow>emptyList();
    }


    private java.util.List<VariableRow> getDataContext(YAWLAtomicTask task) {
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
        if (_rulePanel != null) _rulePanel.clearInputs();
        if (_conclusionPanel != null) _conclusionPanel.setConclusion(null);
        if (_mode == DialogMode.Replacing && _dataContextPanel != null) {
            _dataContextPanel.removeAll();
        }
    }


    protected Element getDataElement() {
        YSpecificationID specID = SpecificationModel.getHandler()
                .getSpecification().getSpecificationID();
        RuleType rule = getSelectedRule();
        YAWLAtomicTask task = getSelectedTask();
        String taskID = task != null ? task.getID() : null;
        return getDataElement(specID, rule, taskID);
    }


    protected Element getDataElement(YSpecificationID specID, RuleType rule, String taskID) {
        String dataRootName = rule.isCaseLevelType() ? specID.getUri() : taskID;
        return _dataContextPanel.getDataElement(dataRootName);
    }


    private java.util.List<VariableRow> getCornerstoneRows(RdrNode rdrNode) {
        java.util.List<VariableRow> rows = getDataContext(getSelectedTask());
        XNode cornerstoneNode = getCornerstoneNode(rdrNode);
        if (cornerstoneNode != null) {
            for (VariableRow row : rows) {
                XNode rowNode = cornerstoneNode.getChild(row.getName());
                if (rowNode != null) {
                    String value = rowNode.hasChildren() ? rowNode.toPrettyString() :
                            rowNode.getText();
                    row.setValue(value);
                }
            }
        }
        return rows;
    }


    private boolean validateConclusion() {
        return _conclusionPanel.validateConclusion().isEmpty();
    }

}
