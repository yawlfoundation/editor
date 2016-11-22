package org.yawlfoundation.yawl.views.graph;

import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.Triple;

import javax.swing.*;
import java.awt.*;
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

    public DataViewDialog() {
        super();
        setTitle("Task Data I/O Graph");
    }


    protected boolean loadTriples() {
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
        try {
            java.util.List<Triple> triples = processTriples(
                    OntologyHandler.swrlQuery("readsDataFrom"), true);
            triples.addAll(processTriples(
                    OntologyHandler.swrlQuery("writesDataTo"), false));
            setTriples(triples);
        }
        catch (Exception oqe) {
            oqe.printStackTrace();
        }
        return ! getTriples().isEmpty();
    }

    protected JPanel getFilterPanel() {
        _varTable = createTable(getVariables());
        _taskTable = createTable(getTasks());

        JPanel panel = new JPanel(new GridLayout(0,1));
        panel.setBackground(Color.WHITE);
        panel.add(getTablePanel(_varTable, "Variables"));
        panel.add(getTablePanel(_taskTable, "Tasks"));
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
        if (subject.startsWith("TASK:")) subject = subject.substring(6);
        if (object.startsWith("TASK:")) object = object.substring(6);
        return selected.contains(subject) || selected.contains(object);
    }


    private java.util.List<Triple> processTriples(java.util.List<Triple> triples, boolean invert) {
        java.util.List<Triple> processed = new ArrayList<Triple>();
        for (Triple triple : triples) {
            String subject = "TASK: " +  triple.getSubject();
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
            vars.add(subject.startsWith("TASK:") ? object : subject);
        }
        return vars;
    }

    private Set<String> getTasks() {
        Set<String> tasks = new HashSet<String>();
        for (Triple triple : getTriples()) {
            String subject = triple.getSubject();
            String object = triple.getObject();
            String value = subject.startsWith("TASK:") ? subject : object;
            tasks.add(value.substring(6));   // rem "TASK: "
        }
        return tasks;
    }

}
