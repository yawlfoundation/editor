package org.yawlfoundation.yawl.worklet.dialog;

import org.jdom2.Element;
import org.yawlfoundation.yawl.editor.ui.properties.data.StatusPanel;
import org.yawlfoundation.yawl.editor.ui.properties.data.VariableRow;
import org.yawlfoundation.yawl.editor.ui.properties.dialog.component.MiniToolBar;
import org.yawlfoundation.yawl.schema.XSDType;
import org.yawlfoundation.yawl.util.StringUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

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
        setCondition(null);
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
        if (StringUtil.isNullOrEmpty(condition)) {
            _status.set("Condition Required");
        }
        else {
            _txtCondition.getInputVerifier().verify(_txtCondition);
        }
    }


    public void updateCondition(VariableRow row) {
        if (row != null) {
            String value = row.getValue();
            String dataType = row.getDataType();
            if (value == null) {
                if (XSDType.isNumericType(dataType)) value = "0";
                else if (XSDType.isBooleanType(dataType)) value = "false";
                else value = "";
            }
            if (dataType.equals("string")) {
                value = "\"" + value + "\"";
            }
            String newCondition = row.getName() + " = " + value;
            String currentCondition = getCondition().trim();

            // append instead of replace if current condition ends with a conjunction op
            if (currentCondition.endsWith("&") || currentCondition.endsWith("|")) {
                newCondition = currentCondition + " " + newCondition;
            }

            setCondition(newCondition);
        }
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
                _status.clear();
            }
        });

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                field.setBackground(Color.WHITE);
                _status.clear();
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
