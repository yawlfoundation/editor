package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.dialog.AddRuleDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class AddRuleAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Add Rule");
        putValue(Action.NAME, "Add Rule");
        putValue(Action.LONG_DESCRIPTION,
                "Add a rule to the rule set for the current specification");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
    }


    public void actionPerformed(ActionEvent event) {
        if (WorkletClient.getInstance().isConnected()) {
            new AddRuleDialog().setVisible(true);
        }
        else {
            MessageDialog.error(
                    "Cannot add rule: No current connection to the Worklet Service",
                    "Worklet Service Connection Error");
        }
    }

}
