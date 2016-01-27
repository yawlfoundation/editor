package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.ui.actions.specification.YAWLSpecificationAction;
import org.yawlfoundation.yawl.worklet.dialog.WorkletLoadDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class LoadWorkletAction extends YAWLSpecificationAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Load Worklet");
        putValue(Action.NAME, "Load");
        putValue(Action.LONG_DESCRIPTION, "Download a stored worklet into the editor");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
    }


    public void actionPerformed(ActionEvent event) {
        new WorkletLoadDialog().setVisible(true);
    }

}
