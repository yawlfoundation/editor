package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.worklet.rdrutil.RdrConditionException;
import org.yawlfoundation.yawl.worklet.support.ConditionEvaluator;

import javax.swing.*;
import java.awt.*;

/**
 * @author Michael Adams
 * @date 25/08/15
 */
public class ConditionVerifier extends InputVerifier {

    private final ConditionEvaluator _conditionEvaluator;
    private final ErrorMessageShortener _msgShortener;
    private ConditionPanel _parent;
    private boolean _valid = false;


    protected ConditionVerifier(ConditionPanel parent) {
        _parent = parent;
        _conditionEvaluator = new ConditionEvaluator();
        _msgShortener = new ErrorMessageShortener();
    }


    @Override
    public boolean verify(JComponent input) {
        validateCondition((JTextField) input);
        return true;                               // always allow user to leave field
    }


    public boolean hasValidContent() { return _valid; }

    public void invalidate() { _valid = false; }


    private void validateCondition(JTextField textField) {
        String errMsg = null;
        String condition = textField.getText();
        if (condition.isEmpty()) {
            errMsg = "Condition required";
        }
        else {
            try {
                _valid = _conditionEvaluator.evaluate(textField.getText(),
                        _parent.getDataElement(), true);
                if (!_valid) {
                    errMsg = "Condition does not evaluate to true";
                }
            }
            catch (RdrConditionException rce) {
                errMsg = _msgShortener.getConditionError(rce.getMessage());
                _valid = false;
            }
        }
        setVisuals(textField, errMsg);
        _parent.setValidationResponse(errMsg);
    }


    private void setVisuals(JTextField textField, String errMsg) {
        textField.setToolTipText(errMsg);
        textField.setBackground(errMsg != null ? Color.PINK : Color.WHITE);
    }

}
