package org.yawlfoundation.yawl.views.graph;

import org.yawlfoundation.yawl.editor.core.resourcing.ResourceDataSet;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.resourcing.resource.Role;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.Triple;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 4/11/16
 */
public class DataViewDialog extends AbstractViewDialog {

    private JTable _varTable;
    private JTable _taskTable;
    private JPanel _taskTablePanel;


    public DataViewDialog() {
        super();
        setTitle("Data I/O Graph");
    }


    protected boolean loadTriples() {
        return loadTaskTriples();
    }


    private boolean loadTaskTriples() {
        if (loadTriples("readsDataFrom", "writesDataTo", "TASK: ")) {
            updateTables();
            setTablePanelTitle("Tasks");
            return true;
        }
        return false;
    }

    private boolean loadRoleTriples() {
        if (loadTriples("roleReadsDataFrom", "roleWritesDataTo", "ROLE: ")) {
            updateTables();
            setTablePanelTitle("Roles");
            return true;
        }
        return false;
    }


    private boolean loadTriples(String predFrom, String predTo, String prefix) {
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
        try {
            java.util.List<Triple> triples = processTriples(
                    OntologyHandler.sparqlQuery(predFrom), true, prefix);
            triples.addAll(processTriples(
                    OntologyHandler.sparqlQuery(predTo), false, prefix));
            setTriples(triples);
        }
        catch (Exception oqe) {
            oqe.printStackTrace();
        }
        return ! getTriples().isEmpty();
    }


    private void updateTables() {
        if (_taskTable != null) {
            getTableModel(_taskTable).setValues(getTasksOrRoles());
        }
        if (_varTable != null) {
            getTableModel(_varTable).setValues(getVariables());
        }
    }

    protected JPanel getFilterPanel() {
        _varTable = createTable(getVariables());
        _taskTable = createTable(getTasksOrRoles());

        JPanel tablePanel = new JPanel(new GridLayout(0,1));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.add(getTablePanel(_varTable, "Variables"));
        _taskTablePanel = getTablePanel(_taskTable, "Tasks");
        tablePanel.add(_taskTablePanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getChoicesPanel(), BorderLayout.SOUTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }


    protected void filter(SelectionTableModel model) {
        java.util.List<Triple> varFiltered = filter(getTriples(), getTableModel(_varTable));
        java.util.List<Triple> taskFiltered = filter(getTriples(), getTableModel(_taskTable));
        java.util.List<Triple> combined = new ArrayList<Triple>();
        if (! (varFiltered.isEmpty() || taskFiltered.isEmpty())) {
            for (Triple triple : varFiltered) {
                if (taskFiltered.contains(triple)) {
                    combined.add(triple);
                }
            }
        }
        refreshView(combined);
    }


    protected boolean isFilterMatch(Triple triple, Set<String> selected) {
        String subject = triple.getSubject();
        String object = triple.getObject();
        if (subject.contains(":")) subject = subject.substring(6);
        if (object.contains(":")) object = object.substring(6);
        return selected.contains(subject) || selected.contains(object);
    }


    private JPanel getChoicesPanel() {
        JRadioButton btnTasks = new JRadioButton("Tasks");
        btnTasks.setActionCommand("tasks");
        btnTasks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTaskTriples();
            }
        });
        btnTasks.setSelected(true);

        JRadioButton btnRoles = new JRadioButton("Roles");
        btnRoles.setActionCommand("roles");
        btnRoles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRoleTriples();
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(btnTasks);
        group.add(btnRoles);

        JPanel choicesPanel = new JPanel(new GridLayout(0, 1));
        choicesPanel.setBorder(new TitledBorder("Relation"));
        choicesPanel.add(btnTasks);
        choicesPanel.add(btnRoles);
        return choicesPanel;
    }


    private java.util.List<Triple> processTriples(java.util.List<Triple> triples,
                                                  boolean invert, String prefix) {
        java.util.List<Triple> processed = new ArrayList<Triple>();
        for (Triple triple : triples) {
            String subject = prefix +  getRoleName(triple.getSubject());
            Triple newTriple = new Triple(subject, triple.getPredicate(), triple.getObject());
            if (invert) newTriple.invert();
            processed.add(newTriple);
        }
        return processed;
    }


    private Set<String> getVariables() {
        Set<String> vars = new HashSet<String>();
        for (Triple triple : getTriples()) {
            String subject = triple.getSubject();
            String object = triple.getObject();
            vars.add(subject.contains(":") ? object : subject);
        }
        return vars;
    }

    private Set<String> getTasksOrRoles() {
        Set<String> labels = new HashSet<String>();
        for (Triple triple : getTriples()) {
            String subject = triple.getSubject();
            String object = triple.getObject();
            String value = subject.contains(":") ? subject : object;
            labels.add(value.substring(6));             // rem "TASK: "  or "ROLE: "
        }
        return labels;
    }


    private String getRoleName(String roleID) {
        if (roleID != null && roleID.startsWith("RO")) {
            Role role = ResourceDataSet.getRole(roleID);
            if (role != null) {
                return role.getName();
            }
        }
        return roleID;
    }


    protected void setTablePanelTitle(String title) {
        if (_taskTablePanel != null) {
            ((TitledBorder) _taskTablePanel.getBorder()).setTitle(title);
            _taskTablePanel.invalidate();
            _taskTablePanel.repaint();
        }
    }

}
