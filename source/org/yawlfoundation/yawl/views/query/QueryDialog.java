package org.yawlfoundation.yawl.views.query;

import org.yawlfoundation.yawl.editor.core.resourcing.ResourceDataSet;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.util.ButtonUtil;
import org.yawlfoundation.yawl.resourcing.resource.Role;
import org.yawlfoundation.yawl.views.graph.GenericViewDialog;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.Triple;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Adams
 * @date 16/11/16
 */
public class QueryDialog extends JDialog implements ActionListener {

    private JComboBox _subject;
    private JComboBox _predicate;
    private JComboBox _object;
    private ColorTextPane _results;
    private JLabel _status;
    private JButton _btnGraph;
    private java.util.List<Triple> _triples;

    private final Map<JComboBox, String> _lastEntry;

    private final Color PRED_COLOR = new Color(0,102,204);


    public QueryDialog() {
        super(YAWLEditor.getInstance());
        setTitle("Query Tool");
        _lastEntry = new HashMap<JComboBox, String>();
        setContentPane(getContent());
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
        setMinimumSize(new Dimension(getSize().width, 400));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("comboBoxChanged")) {
            updateCombo((JComboBox) e.getSource());
        }
        else if (cmd.equals("Run")) {
            run();
        }
        else if (cmd.equals("Clear")) {
            clear();
        }
        else if (cmd.equals("Define")) {
            define();
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
        panel.setBorder(new EmptyBorder(5,5,0,5));
        panel.add(getEntryPanel(), BorderLayout.NORTH);
        panel.add(getCentrePanel(), BorderLayout.CENTER);
        panel.add(getButtonBar(), BorderLayout.SOUTH);
        return panel;
    }


    private JPanel getEntryPanel() {
        _subject = createCombo();
        _predicate = createCombo();
        _object = createCombo();

        JLabel l1 = new JLabel("Subject");
        JLabel l2 = new JLabel("Predicate");
        JLabel l3 = new JLabel("Object");

        l1.setLabelFor(_subject);
        l2.setLabelFor(_predicate);
        l3.setLabelFor(_object);

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        GroupLayout.Group yLabelGroup = layout.createParallelGroup(
                GroupLayout.Alignment.TRAILING);
        hGroup.addGroup(yLabelGroup);
        GroupLayout.Group yFieldGroup = layout.createParallelGroup();
        hGroup.addGroup(yFieldGroup);
        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        layout.setVerticalGroup(vGroup);

        int p = GroupLayout.PREFERRED_SIZE;

        yLabelGroup.addComponent(l1);
        yLabelGroup.addComponent(l2);
        yLabelGroup.addComponent(l3);

        yFieldGroup.addComponent(_subject, p, p, p);
        yFieldGroup.addComponent(_predicate, p, p, p);
        yFieldGroup.addComponent(_object, p, p, p);

        vGroup.addGroup(layout.createParallelGroup().
                    addComponent(l1).
                    addComponent(_subject, p, p, p));

        vGroup.addGroup(layout.createParallelGroup().
                    addComponent(l2).
                    addComponent(_predicate, p, p, p));

        vGroup.addGroup(layout.createParallelGroup().
                    addComponent(l3).
                    addComponent(_object, p, p, p));

        return panel;
    }


    private JScrollPane getResultsPanel() {
        _results = new ColorTextPane();
        _results.addColor(PRED_COLOR);
        _results.setEditable(false);
        _results.setBackground(Color.WHITE);

        // allow Ctrl-C copy for non-editable text area
        _results.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isMetaDown() || e.isControlDown()) {
                     if (e.getKeyCode() == KeyEvent.VK_C) {
                         _results.setEditable(true);
                         _results.copy();
                         _results.setEditable(false);
                     }
                 }
                super.keyReleased(e);
            }
        });

        return new JScrollPane(_results);
    }


    private JPanel getCentrePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        _status = new JLabel();
        panel.add(_status, BorderLayout.SOUTH);
        panel.add(getResultsPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel getButtonBar() {
        JPanel panel = new JPanel();
        panel.add(ButtonUtil.createButton("Exit", this));
        panel.add(ButtonUtil.createButton("Clear", this));
        panel.add(ButtonUtil.createButton("Define", this));

        _btnGraph = ButtonUtil.createButton("Graph", this);
        _btnGraph.setEnabled(false);
        panel.add(_btnGraph);

        JButton btnRun = ButtonUtil.createButton("Run", this);
        getRootPane().setDefaultButton(btnRun);
        panel.add(btnRun);
        return panel;
    }


    private JComboBox createCombo() {
        JComboBox cbx = new JComboBox();
        cbx.setEditable(true);
        cbx.setPrototypeDisplayValue("0123456789012345678901234567890123456789");
        cbx.addActionListener(this);
        _lastEntry.put(cbx, null);
        return cbx;
    }


    private void clear() {
        _subject.setSelectedItem(null);
        _predicate.setSelectedItem(null);
        _object.setSelectedItem(null);
        _results.setText(null);
    }


    private void updateCombo(JComboBox combo) {
        String entry = (String) combo.getSelectedItem();
        if (entry != null && entry.isEmpty()) {
            entry = null;
        }
        _lastEntry.put(combo, entry);
        if (entry == null) return;

        // if unique entry, add it to the combo's items
        for (int i=0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(entry)) {
                return;
            }
        }
        combo.removeActionListener(this);
        combo.insertItemAt(combo.getSelectedItem(), 0);
        combo.addActionListener(this);
    }


    private void run() {
        _results.setText(null);
        String s = _lastEntry.get(_subject);
        String p = _lastEntry.get(_predicate);
        String o = _lastEntry.get(_object);

        java.util.List<Triple> results = OntologyHandler.sparqlQuery(s, p, o);
        if (! results.isEmpty()) {
            displayResults(results);
        }
        else {
            _status.setText("No triples found matching query");
            _btnGraph.setEnabled(false);
        }
    }


    private void displayResults(java.util.List<Triple> results) {
        Collections.sort(results, new TripleSorter());
        _triples = results;
        _results.setEditable(true);
        _btnGraph.setEnabled(true);
        for (Triple triple : results) {
            _results.append(getName(triple.getSubject()));
            _results.append(" ");
            String pred = triple.getPredicate();
            _results.append(pred.substring(pred.indexOf('#') + 1), PRED_COLOR);
            _results.append(" ");
            _results.append(getName(triple.getObject()));
            _results.append(" ");
            _results.append("\n");
        }
        _results.setEditable(false);
        _status.setText(results.size() + " triple" +
                (results.size() > 1 ? "s" : "") + " returned");
    }


    private String getName(String roleID) {
        if (roleID != null && roleID.startsWith("RO")) {
            Role role = ResourceDataSet.getRole(roleID);
            if (role != null) {
                return role.getName();
            }
        }
        return roleID;
    }


    private void define() {
        new SparqlQueryDialog().setVisible(true);
    }


    private void graph() {
        new GenericViewDialog(_triples).setVisible(true);
    }


    /*********************************************************************/

    class TripleSorter implements Comparator<Triple> {
        @Override
        public int compare(Triple o1, Triple o2) {
            int c = compare(o1.getSubject(), o2.getSubject());
            if (c == 0) c = compare(o1.getPredicate(), o2.getPredicate());
            if (c == 0) c = compare(o1.getObject(), o2.getObject());
            return c;
        }

        private int compare(String a, String b) {
            if (a == null && b == null) return 0;
            if (a == null) return -1;
            if (b == null) return 1;
            return a.compareTo(b);
        }
    }

}
