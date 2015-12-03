package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class RemoveRuleSetAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Remove Rule Set");
        putValue(Action.NAME, "Remove Rule Set");
        putValue(Action.LONG_DESCRIPTION, "Remove an existing Rule Set from the Worklet Service");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
    }

    public void actionPerformed(ActionEvent event) {
    //    new AddRuleDialog(selectedTask).setVisible(true);
    }

}
