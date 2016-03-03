package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.dialog.ViewTreeDialog;
import org.yawlfoundation.yawl.worklet.rdr.RdrSet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

class ViewRuleSetAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "View Rule Set");
        putValue(Action.NAME, "View Rule Set");
        putValue(Action.LONG_DESCRIPTION, "View the rule set for the current specification");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_V);
    }

    public void actionPerformed(ActionEvent event) {
        RdrSet rdrSet = loadRdrSet();
        if (rdrSet != null) {
            new ViewTreeDialog(rdrSet).setVisible(true);
        }
    }


    private RdrSet loadRdrSet() {
        try {
            YSpecificationID specID = SpecificationModel.getHandler().getID();
            String s = WorkletClient.getInstance().getRdrSet(specID);
            if (s != null) {
                RdrSet rdrSet = new RdrSet(specID);
                rdrSet.fromXML(s, false);
                return rdrSet;
            }

        }
        catch (IOException ioe) {
            MessageDialog.error("Unable to load rule set from worklet service: " +
                    ioe.getMessage(), "Rule Set Load Error");
        }
        return null;
    }


}
