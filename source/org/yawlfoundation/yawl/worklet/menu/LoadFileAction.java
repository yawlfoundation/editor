package org.yawlfoundation.yawl.worklet.menu;

import com.l2fprod.common.swing.JDirectoryChooser;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.actions.YAWLBaseAction;
import org.yawlfoundation.yawl.editor.ui.specification.validation.ValidationMessage;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.worklet.settings.SettingsStore;
import org.yawlfoundation.yawl.worklet.upload.UpLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

class LoadFileAction extends YAWLBaseAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Upload Files");
        putValue(Action.NAME, "Upload Files");
        putValue(Action.LONG_DESCRIPTION, "Upload worklets and/or rule sets from file");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
    }


    public void actionPerformed(ActionEvent event) {
        File file = getSelectedFile();
        if (file != null) {
            UpLoader upLoader = new UpLoader();
            java.util.List<String> errors = upLoader.upload(file);
            showResults(upLoader, errors);
        }
    }


    private File getSelectedFile() {
        JDirectoryChooser chooser = new JDirectoryChooser(SettingsStore.getLastLoadFilePath());
        chooser.setDialogTitle("UpLoad Worklets and/or Rule Sets");
        chooser.setShowingCreateDirectory(false);

        int response = chooser.showDialog(YAWLEditor.getInstance(), "Upload");
        if (response != JFileChooser.CANCEL_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null && file.exists()) {
                SettingsStore.setLastLoadFilePath(file.getAbsolutePath());
                return file;
            }
        }
        return null;
    }


    private void showResults(UpLoader upLoader, java.util.List<String> errors) {
        int worklets = upLoader.getWorkletCount();
        int rules = upLoader.getRulesCount();
        StringBuilder s = new StringBuilder();
        if (worklets > 0) {
            s.append(worklets).append(" worklet specifications successfully uploaded.\n");
        }
        if (rules > 0) {
            s.append(rules).append(" rule sets successfully uploaded.\n");
        }
        if (! errors.isEmpty()) {
            s.append(errors.size()).append(" files could not be loaded.\n");
            s.append("Please refer to the problem list below for details.\n");
        }
        if (s.length() == 0) {
            s.append("No worklet specifications or rule sets\nfound in the selected directory.");
        }

        // show success or error messages in problems pane
        YAWLEditor.getInstance().showProblemList("Worklet Upload", getResults(errors));

        MessageDialog.info(s.toString(), "Upload Result");
    }



    private java.util.List<ValidationMessage> getResults(java.util.List<String> errors) {
        java.util.List<ValidationMessage> msgList = new ArrayList<ValidationMessage>();
        for (String error : errors) {
             msgList.add(new ValidationMessage(error));
        }
        return msgList;
    }

}
