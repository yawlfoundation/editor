package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.core.validation.Validator;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.specification.io.SpecificationWriter;
import org.yawlfoundation.yawl.editor.ui.specification.validation.SpecificationValidator;
import org.yawlfoundation.yawl.editor.ui.specification.validation.ValidationMessage;
import org.yawlfoundation.yawl.editor.ui.specification.validation.ValidationResultsParser;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

class SaveWorkletAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Save as Worklet");
        putValue(Action.NAME, "Save");
        putValue(Action.LONG_DESCRIPTION, "Store the current specification as a worklet");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);
    }


    public void actionPerformed(ActionEvent event) {
        YSpecification spec = new SpecificationWriter().cleanSpecification();
        if (isValidSpecification(spec)) {
           try {
               WorkletClient.getInstance().addWorklet(spec.getSpecificationID(),
                       SpecificationModel.getHandler().getSpecificationXML(true));
               MessageDialog.info("Worklet successfully added.", "Upload Worklet");
           }
           catch (IOException ioe) {
               MessageDialog.error(ioe.getMessage(), "Worklet Add Error");
           }
        }
        else {
            MessageDialog.error("Could not save worklet because it is not valid.\n" +
                    "Please review the problems in the Validate window below.",
                    "Invalid Specification");
        }
    }


    private boolean isValidSpecification(YSpecification specification) {
        List<String> errors = new SpecificationValidator().getValidationResults(
                specification, Validator.ERROR_MESSAGES);
        List<ValidationMessage> messages = new ValidationResultsParser().parse(errors);
        YAWLEditor.getInstance().showProblemList("Validation Results", messages);
        return errors.isEmpty();
    }
}
