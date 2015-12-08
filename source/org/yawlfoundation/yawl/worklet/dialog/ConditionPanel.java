package org.yawlfoundation.yawl.worklet.dialog;

import org.jdom2.Element;
import org.yawlfoundation.yawl.editor.ui.properties.data.StatusPanel;
import org.yawlfoundation.yawl.editor.ui.properties.dialog.component.MiniToolBar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author Michael Adams
 * @date 8/12/2015
 */
public class ConditionPanel extends JPanel implements ActionListener {

    private JTextField _txtCondition;
    private StatusPanel _status;
    private NodePanel _parent;


    public ConditionPanel(NodePanel parent) {
        super();
        _parent = parent;
        setLayout(new BorderLayout());
        setContent();
        _status.set("what");
    }


    @Override
    public void actionPerformed(ActionEvent e) {             // enter key pressed
        _txtCondition.getInputVerifier().verify(_txtCondition);
    }


    public void clearInputs() { _txtCondition.setText(null); }


    public boolean hasValidContent() {
        return ((ConditionVerifier) _txtCondition.getInputVerifier()).hasValidContent();
    }


    public JTextField getField() { return _txtCondition; }

    public JLabel getPrompt() {
        JLabel label = new JLabel("Condition:", JLabel.LEADING);
        label.setBorder(new EmptyBorder(0,0,16,0));
        return label;
    }

    public NodePanel getParent() { return _parent; }


    public Element getDataElement() {
        return _parent.getDataElement();
    }


    public String getCondition() { return _txtCondition.getText(); }


    public void setCondition(String condition) {
        _txtCondition.setText(condition);
        _txtCondition.getInputVerifier().verify(_txtCondition);
    }


    // from ConditionVerifier#validate
    public void setStatus(String text) {
        if (text != null) {
            _status.set(text);
        }
        else _status.clear();

        getParent().getDialog().enableButtons();
    }


    private void setContent() {
        _txtCondition = getConditionField();
        add(_txtCondition, BorderLayout.CENTER);
        add(populateToolBar(), BorderLayout.SOUTH);
    }


    private JTextField getConditionField() {
        final JTextField field = new JTextField();
        field.setInputVerifier(new ConditionVerifier(this));
        field.addActionListener(this);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                field.setBackground(Color.WHITE);
                field.setToolTipText(null);
            }
        });
        return field;
    }


    private JToolBar populateToolBar() {
        MiniToolBar toolbar = new MiniToolBar(null);
        toolbar.addSeparator(new Dimension(8,16));
        _status = new StatusPanel(null);
        toolbar.add(_status);
        return toolbar;
    }

}
