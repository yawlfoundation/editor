package org.yawlfoundation.yawl.worklet.dialog;

import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.swing.SpecificationListModel;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.support.WorkletInfo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * @author Michael Adams
 * @date 22/01/2016
 */
public class RemoveOrphansDialog extends WorkletLoadDialog {


    public RemoveOrphansDialog() {
        super();
        setTitle("Remove Selected Unreferenced Worklets");
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        setVisible(false);
        if (e.getActionCommand().equals("OK")) {
            removeSelections();
        }
    }


    @Override
    protected JList getList() {
        try {
            java.util.List<WorkletInfo> orphanList =
                    WorkletClient.getInstance().getOrphanedWorklets();
            return new JList(new WorkletSpecificationListModel(orphanList));

        }
        catch (IOException ioe) {
            showError("Failed to get list of unreferenced worklets from the Worklet Service: ",
                    ioe);
        }
        return new JList();
    }


    private void removeSelections() {
        try {
            for (int index : listBox.getSelectedIndices()) {
                YSpecificationID selectedID =
                        ((SpecificationListModel) listBox.getModel()).getSelectedID(index);
                WorkletClient.getInstance().removeWorklet(selectedID.getKey());
            }
            int count = listBox.getSelectedIndices().length;
            MessageDialog.info(count + " worklet" + (count == 1 ? "s" : "") + " removed.",
                    "Remove Unreferenced Worklets");
        }
        catch (IOException ioe) {
            showError("Failed to remove all selections", ioe);
        }
    }


}
