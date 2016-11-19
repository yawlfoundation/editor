package org.yawlfoundation.yawl.views.query;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.Triple;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Adams
 * @date 16/11/16
 */
public class QueryDialog extends JDialog implements ActionListener, KeyListener {

    private JComboBox _subject;
    private JComboBox _predicate;
    private JComboBox _object;
    private JTextArea _results;

    private Map<JComboBox, String> _lastEntry;


    public QueryDialog() {
        super(YAWLEditor.getInstance());
        setTitle("Query Tool");
        _lastEntry = new HashMap<JComboBox, String>();
        setContentPane(getContent());
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
        setMinimumSize(getSize());
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
            _subject.setSelectedItem(null);
            _predicate.setSelectedItem(null);
            _object.setSelectedItem(null);
            _results.setText(null);
        }
        else if (cmd.equals("Exit")) {
            setVisible(false);
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            run();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}


    private JPanel getContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5,5,0,5));
        panel.add(getEntryPanel(), BorderLayout.NORTH);
        panel.add(getResultsPanel(), BorderLayout.CENTER);
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
        _results = new JTextArea(20, 10);
        _results.setEditable(false);
        _results.setBackground(Color.WHITE);
        return new JScrollPane(_results);
    }


    private JPanel getButtonBar() {
        JPanel panel = new JPanel();
        panel.add(createButton("Exit", this));
        panel.add(createButton("Clear", this));
        panel.add(createButton("Run", this));
        return panel;
    }


    private JComboBox createCombo() {
        JComboBox cbx = new JComboBox();
        cbx.setEditable(true);
        cbx.setPrototypeDisplayValue("0123456789012345678901234567890123456789");
        cbx.addActionListener(this);
        cbx.addKeyListener(this);
        _lastEntry.put(cbx, null);
        return cbx;
    }


    private void updateCombo(JComboBox combo) {
        String entry = (String) combo.getSelectedItem();
        _lastEntry.put(combo, entry.isEmpty() ? null : entry);

        // if unique entry, add it to the combo's items
        if (StringUtil.isNullOrEmpty(entry)) return;
        for (int i=0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(entry)) {
                return;
            }
        }
        combo.removeActionListener(this);
        combo.insertItemAt(combo.getSelectedItem(), 0);
        combo.addActionListener(this);
    }


    private JButton createButton(String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.setActionCommand(label);
        button.setMnemonic(label.charAt(0));
        button.setPreferredSize(new Dimension(70,25));
        button.addActionListener(listener);
        return button;
    }


    private void run() {
        _results.setText(null);
        String s = _lastEntry.get(_subject);
        String p = _lastEntry.get(_predicate);
        String o = _lastEntry.get(_object);

        java.util.List<Triple> results = OntologyHandler.swrlQuery(s, p, o);
        if (results.isEmpty()) {
            _results.append("No triples found matching query.");
            return;
        }

        Collections.sort(results, new TripleSubjectSorter());
        for (Triple triple : results) {
            _results.append(triple.getSubject());
            _results.append(" ");
            String pred = triple.getPredicate();
            _results.append(pred.substring(pred.indexOf('#') + 1));
            _results.append(" ");
            _results.append(triple.getObject());
            _results.append(" ");
            _results.append("\n");
        }
    }


    /*********************************************************************/

    class TripleSubjectSorter implements Comparator<Triple> {
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
