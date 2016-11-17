package org.yawlfoundation.yawl.views.query;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.OntologyQueryException;
import org.yawlfoundation.yawl.views.ontology.Triple;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Michael Adams
 * @date 16/11/16
 */
public class QueryDialog extends JDialog implements ActionListener {

    private JTextField _subject;
    private JTextField _predicate;
    private JTextField _object;
    private JTextArea _results;


    public QueryDialog() {
        super(YAWLEditor.getInstance());
        setTitle("Query Tool");
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
        if (cmd.equals("Execute")) {
            execute();
        }
        else if (cmd.equals("Clear")) {
            _subject.setText(null);
            _predicate.setText(null);
            _object.setText(null);
            _results.setText(null);
        }
        else if (cmd.equals("Close")) {
            setVisible(false);
        }
    }

    private JPanel getContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5,5,0,5));
        panel.add(getEntryPanel(), BorderLayout.NORTH);
        panel.add(getResultsPanel(), BorderLayout.CENTER);
        panel.add(getButtonBar(), BorderLayout.SOUTH);
        return panel;
    }


    private JPanel getEntryPanel() {
        _subject = new JTextField(30);
        _predicate = new JTextField(30);
        _object = new JTextField(30);

        JLabel l1 = new JLabel("Subject");
        JLabel l2 = new JLabel("Predicate");
        JLabel l3 = new JLabel("Object");

        l1.setBorder(new EmptyBorder(7,0,3,0));
        l2.setBorder(new EmptyBorder(5,0,5,0));
        l3.setBorder(new EmptyBorder(3,0,7,0));

        l1.setLabelFor(_subject);
        l2.setLabelFor(_predicate);
        l3.setLabelFor(_object);


        JPanel labelPanel = new JPanel(new GridLayout(3,1, 50, 5));
        labelPanel.setBorder(new EmptyBorder(0,0,5,0));
        labelPanel.add(l1);
        labelPanel.add(l2);
        labelPanel.add(l3);

        JPanel inputPanel = new JPanel(new GridLayout(3,1, 5, 5));
        inputPanel.setBorder(new EmptyBorder(0,0,5,5));
        inputPanel.add(_subject);
        inputPanel.add(_predicate);
        inputPanel.add(_object);

        JPanel panel = new JPanel();
        panel.add(labelPanel);
        panel.add(inputPanel);
        return panel;
    }


    private JScrollPane getResultsPanel() {
        _results = new JTextArea(20, 10);
        return new JScrollPane(_results);
    }


    private JPanel getButtonBar() {
        JPanel panel = new JPanel();
        panel.add(createButton("Close", this));
        panel.add(createButton("Clear", this));
        panel.add(createButton("Execute", this));
        return panel;
   }


    private JButton createButton(String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.setActionCommand(label);
        button.setMnemonic(label.charAt(0));
        button.setPreferredSize(new Dimension(70,25));
        button.addActionListener(listener);
        return button;
    }


    private void execute() {
        _results.setText(null);
        String s = _subject.getText();
        String p = _predicate.getText();
        String o = _object.getText();

        try {
            for (Triple triple : OntologyHandler.swrlQuery(s, p, o)) {
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
        catch (OntologyQueryException oqe) {
            oqe.printStackTrace();
        }
    }

}
