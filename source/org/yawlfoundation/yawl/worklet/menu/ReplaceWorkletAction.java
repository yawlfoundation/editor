package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.ui.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.dialog.ReplaceWorkletDialog;
import org.yawlfoundation.yawl.worklet.selection.WorkletRunner;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;

class ReplaceWorkletAction extends YAWLBaseAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Replace Worklet");
        putValue(Action.NAME, "Replace Worklet");
        putValue(Action.LONG_DESCRIPTION, "Replace a currently executing worklet");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
    }


    public void actionPerformed(ActionEvent event) {
        java.util.List<WorkletRunner> runners = getRunners();
        if (! (runners == null || runners.isEmpty())) {
            new ReplaceWorkletDialog(runners).setVisible(true);
        }
    }


    private java.util.List<WorkletRunner> getRunners() {
        try {
            java.util.List<WorkletRunner> runners =
                    WorkletClient.getInstance().getRunningWorkletList();
            if (! (runners == null || runners.isEmpty())) {
                return runners;
            }
            showNoWorkletsInfo();
        }
        catch (IOException ioe) {
            String msg = ioe.getMessage();
            if ("No worklet instances currently running".equals(msg)) {
                showNoWorkletsInfo();
            }
            else {
                MessageDialog.error(msg, "Service Error");
            }
        }
        return Collections.emptyList();
    }


    private void showNoWorkletsInfo() {
        MessageDialog.info("There are no currently executing worklets to replace.",
                "Service Information");
    }

}
