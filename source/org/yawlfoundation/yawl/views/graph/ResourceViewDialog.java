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
public class ResourceViewDialog extends AbstractViewDialog implements ActionListener {

    private static final String PASSES_DATA_TO = "passesDataTo";
    private static final String PASSES_DATA_TO_INDIRECTLY = "passesDataToIndirectly";
    private static final String PASSES_WORK_TO = "passesWorkTo";
    private static final String PASSES_WORK_TO_INDIRECTLY = "passesWorkToIndirectly";

    private JTable _table;


    public ResourceViewDialog() {
        super();
        setTitle("Role Relation Graph");
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("data")) {
            loadTriples(PASSES_DATA_TO_INDIRECTLY);
        }
        else if (cmd.equals("dataStrict")) {
            loadTriples(PASSES_DATA_TO);
        }
        else if (cmd.equals("flow")) {
            loadTriples(PASSES_WORK_TO_INDIRECTLY);
        }
        else if (cmd.equals("flowStrict")) {
            loadTriples(PASSES_WORK_TO);
        }
    }


    @Override
    protected JPanel getFilterPanel() {
        _table = createTable(getRoles(getTriples()));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(getChoicesPanel(), BorderLayout.SOUTH);
        panel.add(getTablePanel(_table, "Roles"), BorderLayout.CENTER);
        return panel;
    }


    @Override
    void filter(SelectionTableModel model) {
        refreshView(filter(getTriples(), getTableModel(_table)));
    }


    protected boolean isFilterMatch(Triple triple, Set<String> selected) {
        return selected.size() > 1 && selected.contains(triple.getSubject()) &&
                selected.contains(triple.getObject());
    }


    protected boolean loadTriples() { return loadTriples(PASSES_DATA_TO_INDIRECTLY); }


    private boolean loadTriples(String predicate) {
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
        try {
            java.util.List<Triple> triples =
                    processTriples(OntologyHandler.swrlQuery(predicate));
            if (_table != null) {
                getTableModel(_table).setValues(getRoles(triples));
            }
            removeSelfReferences(triples);
            setTriples(triples);
        }
        catch (Exception oqe) {
            oqe.printStackTrace();
        }
        return ! getTriples().isEmpty();
    }


    private JPanel getChoicesPanel() {
        JRadioButton btnDF = new JRadioButton("Data-Flow");
        btnDF.setActionCommand("data");
        btnDF.addActionListener(this);
        btnDF.setSelected(true);

        JRadioButton btnDFS = new JRadioButton("Data-Flow (Strict)");
        btnDFS.setActionCommand("dataStrict");
        btnDFS.addActionListener(this);

        JRadioButton btnCF = new JRadioButton("Control-Flow");
        btnCF.setActionCommand("flow");
        btnCF.addActionListener(this);

        JRadioButton btnCFS = new JRadioButton("Control-Flow (Strict)");
        btnCFS.setActionCommand("flowStrict");
        btnCFS.addActionListener(this);

        ButtonGroup group = new ButtonGroup();
        group.add(btnDF);
        group.add(btnDFS);
        group.add(btnCF);
        group.add(btnCFS);

        JPanel choicesPanel = new JPanel(new GridLayout(0, 1));
        choicesPanel.setBorder(new TitledBorder("Relation"));
        choicesPanel.add(btnDF);
        choicesPanel.add(btnDFS);
        choicesPanel.add(btnCF);
        choicesPanel.add(btnCFS);
        return choicesPanel;
    }


    private java.util.List<Triple> processTriples(java.util.List<Triple> triples) {
        java.util.List<Triple> processed = new ArrayList<Triple>();
        for (Triple triple : triples) {
            String subjectRole = getRoleName(triple.getSubject());
            String objectRole = getRoleName(triple.getObject());
            processed.add(new Triple(subjectRole, triple.getPredicate(), objectRole));
        }
        return processed;
    }


    private void removeSelfReferences(java.util.List<Triple> triples) {
        java.util.List<Triple> toRemove = new ArrayList<Triple>();
        for (Triple triple : triples) {
            if (triple.hasDuplicateResources()) {
                toRemove.add(triple);
            }
        }
        triples.removeAll(toRemove);
    }

    private String getRoleName(String roleID) {
        Role role = ResourceDataSet.getRole(roleID);
        return role != null ? role.getName() : roleID;
    }


    private Set<String> getRoles(java.util.List<Triple> triples) {
        Set<String> roles = new HashSet<String>();
        for (Triple triple : triples) {
            roles.add(triple.getSubject());
            roles.add(triple.getObject());
        }
        return roles;
    }

}
