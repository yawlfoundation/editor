package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.ui.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.editor.ui.resourcing.subdialog.ListDialog;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.worklet.client.RdrSetID;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.model.RdrSetIDListModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

abstract class AbstractRuleSetAction extends YAWLBaseAction {


    protected abstract void processSelections(java.util.List<Object> selections)
            throws IOException;

    protected abstract String getTitle();

    protected abstract String getEmptyListMsg();


    public void actionPerformed(ActionEvent event) {
        try {
            java.util.List<RdrSetID> rdrSetIDList = getRdrSetIDs();
            if (rdrSetIDList.isEmpty()) {
                MessageDialog.info(getEmptyListMsg(), getTitle());
            }
            else {
                java.util.List<Object> selections = showListDialog(getRdrSetIDs());
                if (!selections.isEmpty()) {
                    processSelections(selections);
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
                getTitle());
        dialog.setResizable(true);
        dialog.setPreferredSize(new Dimension(550, 500));
        dialog.pack();
        dialog.setVisible(true);
        return dialog.getSelections();
    }

}
