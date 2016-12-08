package org.yawlfoundation.yawl.views.dialog;

import org.apache.jena.reasoner.rulesys.Rule;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.util.ButtonUtil;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.StringReader;

/**
 * @author Michael Adams
 * @date 2/12/16
 */
public class UserRulesDialog extends JDialog implements ActionListener {

    private JTextPane _editPane;
    private JTextArea _errorArea;
    private RuleChecker _ruleChecker;

    private static final String PREFIXES =
            "@prefix : <http://www.semanticweb.org/yawl/ontologies/YSpecificationOntology#>.\n" +
            "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n" +
            "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n\n";


    public UserRulesDialog() {
        super(YAWLEditor.getInstance());
        setTitle("User Defined Rules");
        setContentPane(getContent());
        loadRules();
        _ruleChecker = new RuleChecker(PREFIXES);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(YAWLEditor.getInstance());
        setMinimumSize(new Dimension(getSize().width, 400));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Save")) {
            save();
        }
        else if (cmd.equals("Validate")) {
            syntaxCheck();
        }
        else if (cmd.equals("Close")) {
            setVisible(false);
        }
    }


    private JPanel getContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5,5,5,5));
        panel.add(getEditorPanel(), BorderLayout.CENTER);
        panel.add(getButtonBar(), BorderLayout.SOUTH);
        return panel;
    }


    private JPanel getEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        _editPane = new JTextPane();
        panel.add(new JScrollPane(_editPane), BorderLayout.CENTER);
        _errorArea = new JTextArea(3,50);
        _errorArea.setEditable(false);
        panel.add(new JScrollPane(_errorArea), BorderLayout.SOUTH);
        return panel;
    }


    private void loadRules() {
        if (OntologyHandler.USER_RULE_FILE.exists()) {
            _editPane.setText(StringUtil.fileToString(OntologyHandler.USER_RULE_FILE));
        }
        else {
            _editPane.setText(PREFIXES);
        }
    }


    private boolean syntaxCheck() {
        try {
            BufferedReader reader = new BufferedReader(new StringReader(_editPane.getText()));
            Rule.Parser parser = Rule.rulesParserFromReader(reader);
            Rule.parseRules(parser);
            _ruleChecker.parse(_editPane.getText());
            ok();
            return true;
        }
        catch (Exception rpe) {
            error(rpe.getMessage());
            return false;
        }
    }


    private boolean save() {
        if (syntaxCheck()) {
            StringUtil.stringToFile(OntologyHandler.USER_RULE_FILE, _editPane.getText());
            OntologyHandler.reloadRules();
            return true;
        }
        else {
            MessageDialog.error("Unable to save due to syntax errors.",
                    "Failed to Save");
            return false;
        }
    }


    private JPanel getButtonBar() {
        JPanel panel = new JPanel();
        panel.add(ButtonUtil.createButton("Close", this));
        panel.add(ButtonUtil.createButton("Validate", this));

        JButton btnRun = ButtonUtil.createButton("Save", this);
        getRootPane().setDefaultButton(btnRun);
        panel.add(btnRun);
        return panel;
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
