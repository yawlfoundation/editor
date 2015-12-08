package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.worklet.support.ConditionEvaluator;
import org.yawlfoundation.yawl.worklet.support.RdrConditionException;

import javax.swing.*;
import java.awt.*;

/**
 * @author Michael Adams
 * @date 25/08/15
 */
public class ConditionVerifier extends InputVerifier {

    private final ConditionEvaluator _conditionEvaluator;
    private ConditionPanel _parent;
    private boolean _valid = false;


    protected ConditionVerifier(ConditionPanel parent) {
        _parent = parent;
        _conditionEvaluator = new ConditionEvaluator();
    }


    @Override
    public boolean verify(JComponent input) {
        validateCondition((JTextField) input);
        return true;                               // always allow user to leave field
    }


    public boolean hasValidContent() { return _valid; }


    private void validateCondition(JTextField textField) {
        String errMsg = null;
        String condition = textField.getText();
        if (condition.isEmpty()) {
            errMsg = "Condition required";
        }
        else {
            try {
                _valid = _conditionEvaluator.evaluate(textField.getText(),
                        _parent.getDataElement());
                if (!_valid) {
                    errMsg = "Condition does not evaluate to true";
                }
            }
            catch (RdrConditionException rce) {
                errMsg = rce.getMessage();
                _valid = false;
            }
        }
        setVisuals(textField, errMsg);
        _parent.setStatus(errMsg);
    }


    private void setVisuals(JTextField textField, String errMsg) {
        textField.setToolTipText(errMsg);
        textField.setBackground(errMsg != null ? Color.PINK : Color.WHITE);
    }

}
