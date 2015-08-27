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

    private AddRuleDialog _ruleDialog;

    protected ConditionVerifier(AddRuleDialog ruleDialog) {
        _ruleDialog = ruleDialog;
    }


    @Override
    public boolean verify(JComponent input) {
        validateCondition((JTextField) input);
        return true;                               // always allow user to leave field
    }


    private void validateCondition(JTextField textField) {
        String errMsg = null;
        try {
            boolean evaluatesTrue = new ConditionEvaluator().evaluate(textField.getText(),
                    _ruleDialog.getDataElement());
            if (! evaluatesTrue) {
                errMsg = " Expression must evaluate to true, based on Data Context ";
            }
        }
        catch (RdrConditionException rce) {
            errMsg = " " + rce.getMessage() + " ";
        }
        setVisuals(textField, errMsg);
        showStatus(errMsg);
    }


    private void setVisuals(JTextField textField, String errMsg) {
        textField.setToolTipText(errMsg);
        textField.setBackground(errMsg != null ? Color.PINK : Color.WHITE);
    }


    private void showStatus(String errMsg) {
        _ruleDialog.updateStatus(errMsg != null ? "Invalid Condition: " + errMsg :
                "Condition is valid.");
    }
}
