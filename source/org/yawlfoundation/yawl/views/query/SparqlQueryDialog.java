package org.yawlfoundation.yawl.views.query;

import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.ResultSet;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.swing.JAlternatingRowColorTable;
import org.yawlfoundation.yawl.editor.ui.util.ButtonUtil;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.views.graph.GenericViewDialog;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.Triple;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author Michael Adams
 * @date 2/12/16
 */
public class SparqlQueryDialog extends JDialog implements ActionListener {

    private JTextArea _queryArea;
    private JTextArea _errorArea;
    private SparqlResultsTableModel _tableModel;
    private TableRowSorter<TableModel> _tableSorter;
    private JButton _btnGraph;
    private java.util.List<Triple> _triples = new ArrayList<Triple>();

    private static final String PREFIXES =
            "PREFIX : <http://www.semanticweb.org/yawl/ontologies/YSpecificationOntology#>\n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n\n";


    public SparqlQueryDialog() {
        super(YAWLEditor.getInstance());
        setTitle("Run SPARQL Query");
        setContentPane(getContent());
        setResizable(true);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
        setMinimumSize(new Dimension(getSize().width, 400));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Run")) {
            run();
        }
        else if (cmd.equals("Clear")) {
            clear();
        }
        else if (cmd.equals("Graph")) {
            graph();
        }
        else if (cmd.equals("Exit")) {
            setVisible(false);
        }
    }


    private JPanel getContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5,5,5,5));
        panel.add(getEntryPanel(), BorderLayout.NORTH);
        panel.add(getResultsPanel(), BorderLayout.CENTER);
        return panel;
    }


    private JPanel getEntryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("SPARQL Query"), BorderLayout.NORTH);
        _queryArea = new JTextArea(8,50);
        _queryArea.setLineWrap(true);
        _queryArea.setText(PREFIXES);
        panel.add(new JScrollPane(_queryArea), BorderLayout.CENTER);
        _errorArea = new JTextArea(3,50);
        _errorArea.setEditable(false);
        panel.add(new JScrollPane(_errorArea), BorderLayout.SOUTH);
        return panel;
    }


    private JPanel getResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getTablePanel(), BorderLayout.CENTER);
        panel.add(getButtonBar(), BorderLayout.SOUTH);
        return panel;
    }


    private JPanel getTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        _tableModel = new SparqlResultsTableModel();
        JTable table = new JAlternatingRowColorTable(_tableModel);

        _tableSorter = new TableRowSorter<TableModel>(table.getModel());
        table.setRowSorter(_tableSorter);

        final JCheckBox cb = new JCheckBox("Suppress namespaces in results");
        cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                _tableModel.setSuppressNamespaces(cb.isSelected());
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(cb, BorderLayout.SOUTH);
        return panel;
    }


    private JPanel getButtonBar() {
        JPanel panel = new JPanel();
        panel.add(ButtonUtil.createButton("Exit", this));
        panel.add(ButtonUtil.createButton("Clear", this));

        _btnGraph = ButtonUtil.createButton("Graph", this);
        _btnGraph.setEnabled(false);
        panel.add(_btnGraph);

        JButton btnRun = ButtonUtil.createButton("Run", this);
        getRootPane().setDefaultButton(btnRun);
        panel.add(btnRun);
        return panel;
    }


    private void run() {
        _errorArea.setText(null);
        String query = _queryArea.getText();
        if (!StringUtil.isNullOrEmpty(query)) {
            try {
                ResultSet resultSet = OntologyHandler.runSparqlQuery(query);
                _tableModel.setValues(resultSet);
                setSortKeys(resultSet.getResultVars().size());
                ok();
            }
            catch (QueryParseException qpe) {
                error(qpe.getMessage());
            }
        }
        else {
            error("No query defined");
        }
    }


    private void clear() {
        _queryArea.setText(PREFIXES);
        _errorArea.setText(null);
        _tableModel.clear();
    }


    private void graph() {
        new GenericViewDialog(_triples).setVisible(true);
    }


    private void setSortKeys(int colCount) {
        java.util.List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        for (int i=0; i < colCount; i++) {
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
        }
        _tableSorter.setSortKeys(sortKeys);
    }


    private void error(String msg) {
        _errorArea.setForeground(Color.RED);
        _errorArea.setText(msg);
    }

    private void ok() {
        _errorArea.setForeground(Color.GREEN.darker());
        _errorArea.setText("OK");
    }

}
