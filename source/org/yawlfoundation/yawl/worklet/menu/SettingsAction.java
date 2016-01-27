package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.ui.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.worklet.settings.SettingsDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class SettingsAction extends YAWLBaseAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Settings");
        putValue(Action.NAME, "Settings");
        putValue(Action.LONG_DESCRIPTION, "Worklet Service connection settings");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
    }


    public void actionPerformed(ActionEvent event) {
        new SettingsDialog().setVisible(true);
    }

}
