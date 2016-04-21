package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.worklet.client.RdrSetID;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

class RemoveRuleSetAction extends AbstractRuleSetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Remove Rule Set");
        putValue(Action.NAME, "Remove Rule Set");
        putValue(Action.LONG_DESCRIPTION, "Remove an existing rule set");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
    }


    @Override
    protected String getTitle() { return "Remove Selected Rule Sets"; }


    @Override
    protected String getEmptyListMsg() {
        return "There are no stored rule sets to remove";
    }


    @Override
    protected void processSelections(java.util.List<Object> selections) throws IOException {
        for (Object o : selections) {
            WorkletClient.getInstance().removeRdrSet((RdrSetID) o);
        }
        MessageDialog.info(selections.size() + " rule set" +
                        (selections.size() > 1 ? "s" : "") + " removed",
                "Remove Selected Rule Sets");
    }

}
