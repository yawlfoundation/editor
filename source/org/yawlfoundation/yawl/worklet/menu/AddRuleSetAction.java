package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.settings.SettingsStore;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

class AddRuleSetAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Add Rule Set");
        putValue(Action.NAME, "Add Rule Set");
        putValue(Action.LONG_DESCRIPTION, "Add a Rule Set from file");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
    }


    public void actionPerformed(ActionEvent event) {
        YSpecificationID specID = SpecificationModel.getHandler()
                .getSpecification().getSpecificationID();
        try {
            if (WorkletClient.getInstance().successful(
                    WorkletClient.getInstance().getRdrSet(specID))) {
                MessageDialog.error(
                        "There is an existing rule set for the current specification.",
                        "Unable to Add Rule Set");
            }
            else {
                File xrsFile = getSelectedFile();
                if (xrsFile != null) {
                    String xml = StringUtil.fileToString(xrsFile);
                    if (xml != null) {
                        WorkletClient.getInstance().addRuleSet(specID, xml);
                        MessageDialog.info("Rule set loaded successfully.", "Success");
                    }
                }
                else {
                    MessageDialog.error("Error loading rule set from file.",
                            "Load Rule Set Error");
                }
            }
        }
        catch (IOException ioe) {
            MessageDialog.error(ioe.getMessage(), "Load Rule Set Error");
        }
    }


    private File getSelectedFile() {
        JFileChooser chooser = new JFileChooser(SettingsStore.getLastRuleSetPath());
        chooser.setDialogTitle("Load Worklet Rule Set");
        chooser.setAcceptAllFileFilterUsed(false);

        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".xrs");
            }

            @Override
            public String getDescription() {
                return "Worklet Rule Set files (XRS)";
            }
        });

        int response = chooser.showDialog(YAWLEditor.getInstance(), "Open");
        if (response != JFileChooser.CANCEL_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null && file.isFile() && file.exists()) {
                SettingsStore.setLastRuleSetPath(file.getAbsolutePath());
                return file;
            }
        }
        return null;
    }

}
