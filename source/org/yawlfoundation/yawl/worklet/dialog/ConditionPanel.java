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
    private boolean _shouldValidate;


    public ConditionPanel(NodePanel parent, DialogMode mode) {
        super();
        _parent = parent;
        _shouldValidate = true;
        setLayout(new BorderLayout());
        setContent(mode);
        setMode(mode);
        setCondition(null);
    }


    @Override
    public void actionPerformed(ActionEvent e) {             // enter key pressed
        if (_shouldValidate) {
            getConditionVerifier().verify(_txtCondition);
        }
    }


    public void clearInputs() { setCondition(null); }


    private void setMode(DialogMode mode) {
        boolean enable = mode != DialogMode.Viewing;
        _txtCondition.setEditable(enable);
        _txtCondition.setBackground(Color.WHITE);
        _shouldValidate = enable;
        _status.set(null);
    }


    public boolean hasValidContent() {
        return !_shouldValidate || getConditionVerifier().hasValidContent();
    }


    public JTextField getField() { return _txtCondition; }

    public JLabel getPrompt() {
        JLabel label = new JLabel("Condition:", JLabel.LEADING);
        label.setBorder(new EmptyBorder(0,0,16,0));
        return label;
    }

    public NodePanel getParentPanel() { return _parent; }


    public Element getDataElement() {
        return _parent.getDataElement();
    }


    public String getCondition() { return _txtCondition.getText(); }


    public void setCondition(String condition) {
        _txtCondition.setText(condition);
        if (_shouldValidate) {
            if (StringUtil.isNullOrEmpty(condition)) {
                _status.set("Condition Required");
                getConditionVerifier().invalidate();
            }
            else {
                getConditionVerifier().verify(_txtCondition);
            }
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
    protected void setValidationResponse(String msg) {
        if (_shouldValidate) {
            setStatus(msg);
            getParentPanel().getDialog().enableButtons();
        }
    }

    public void setStatus(String text) {
        if (text != null) {
            _status.set(text);
        }
        else _status.clear();
    }


    private void setContent(DialogMode mode) {
        _txtCondition = getConditionField(mode);
        add(_txtCondition, BorderLayout.CENTER);
        add(populateToolBar(), BorderLayout.SOUTH);
    }


    private JTextField getConditionField(DialogMode mode) {
        final JTextField field = new JTextField(36);
        if (mode != DialogMode.Viewing) {
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

        }
        else {
            field.setBackground(Color.LIGHT_GRAY);
        }
        return field;
    }


    private JToolBar populateToolBar() {
        MiniToolBar toolbar = new MiniToolBar(null);
        toolbar.addSeparator(new Dimension(8,16));
        _status = new StatusPanel(null);
        toolbar.add(_status);
        return toolbar;
    }


    private ConditionVerifier getConditionVerifier() {
        return (ConditionVerifier) _txtCondition.getInputVerifier();
    }

}
