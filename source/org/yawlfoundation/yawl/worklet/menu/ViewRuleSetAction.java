package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.worklet.dialog.ViewTreeDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class ViewRuleSetAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "View Rule Set");
        putValue(Action.NAME, "View Rule Set");
        putValue(Action.LONG_DESCRIPTION, "View the Rule Set for the current specification");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_V);
    }

    public void actionPerformed(ActionEvent event) {
        new ViewTreeDialog().setVisible(true);
    }

}
