package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.resourcing.subdialog.ListDialog;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.worklet.client.RdrSetID;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.model.RdrSetIDListModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

class RemoveRuleSetAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Remove Rule Set");
        putValue(Action.NAME, "Remove Rule Set");
        putValue(Action.LONG_DESCRIPTION, "Remove an existing rule set");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
    }

    public void actionPerformed(ActionEvent event) {
        try {
            java.util.List<RdrSetID> rdrSetIDList = getRdrSetIDs();
            if (rdrSetIDList.isEmpty()) {
                MessageDialog.info("There are no stored rule sets to remove",
                        "Remove Rule Sets");
            }
            else {
                java.util.List<Object> selections = showListDialog(getRdrSetIDs());
                if (!selections.isEmpty()) {
                    removeSets(selections);
                    MessageDialog.info(selections.size() + " rule set" +
                                    (selections.size() > 1 ? "s" : "") + " removed",
                            "Remove Selected Rule Sets");
                }
            }
        }
        catch (IOException ioe) {
            MessageDialog.error(ioe.getMessage(), "Service Error");
        }
    }


    private java.util.List<RdrSetID> getRdrSetIDs() throws IOException {
        return WorkletClient.getInstance().getRdrSetIDs();
    }


    private java.util.List<Object> showListDialog(java.util.List<RdrSetID> rdrSetIDList)
            throws IOException {
        ListDialog dialog = new ListDialog(null, new RdrSetIDListModel(rdrSetIDList),
                "Remove Selected Rule Sets");
        dialog.setResizable(true);
        dialog.setPreferredSize(new Dimension(550, 500));
        dialog.pack();
        dialog.setVisible(true);
        return dialog.getSelections();
    }


    private void removeSets(java.util.List<Object> selections) throws IOException {
        for (Object o : selections) {
            WorkletClient.getInstance().removedRdrSet((RdrSetID) o);
        }
    }

}
