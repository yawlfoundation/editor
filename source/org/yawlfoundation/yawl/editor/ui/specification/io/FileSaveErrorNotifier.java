package org.yawlfoundation.yawl.editor.ui.specification.io;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.editor.ui.util.BuildProperties;
import org.yawlfoundation.yawl.editor.ui.util.ErrorReporter;
import org.yawlfoundation.yawl.reporter.Report;

import javax.swing.*;
import java.io.IOException;

/**
 * @author Michael Adams
 * @date 24/11/2015
 */
public class FileSaveErrorNotifier {

    public FileSaveErrorNotifier() { }


    public void notify(Exception e, String fileName) {
        String errMsg = e.getMessage() != null ? e.getMessage() : "General Error";
        if (errMsg.contains("Permission denied") || errMsg.contains("Access is denied")) {
            showPermissionDeniedError(errMsg);
        }
        else if (errMsg.contains("cannot find the path")) {
            showBadPathError(errMsg);
        }
        else {
            showGeneralError(e, fileName, errMsg);
        }
    }


    private void showPermissionDeniedError(String errMsg) {
        showUserError(errMsg,
                ".\nYou do not have the necessary file permissions to save to that directory.\n"
        );
    }


    private void showBadPathError(String errMsg) {
        String detail = errMsg.contains("zip") ?
                ".\nYou have opened the specification file from inside a compressed (zip) file.\n" :
                ".\n";
        showUserError(errMsg, detail);
    }


    private void showUserError(String errMsg, String detail) {
        MessageDialog.error(
                "Failed to save this specification to " + errMsg + detail +
                "Please use 'File...Save As' to save the specification file to a different location.",
                "Save File Error");
    }


    private void showGeneralError(Exception e, String fileName, String errMsg) {
        String[] options = new String[] {"Don't Send", "Send" };
        String message = "The attempt to save this specification to file failed." +
                "\nError message: " + errMsg +
                "\n\nClick 'Send' to report this issue to the YAWL team.";

        int choice = JOptionPane.showOptionDialog(YAWLEditor.getInstance(), message,
                "Save File Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE,
                null, options, options[1]);

        if (choice == 1) {
            try {
                ErrorReporter er = new ErrorReporter();
                Report report = er.prepare("Failed to save", e);
                report.addContent("FileName", fileName);
                report.add("Version", getVersion());
                er.send(report);
            }
            catch (IOException ioe) {
                //
            }
        }
    }


    private String getVersion() {
        BuildProperties props = new BuildProperties();
        String version = props.getVersion();
        String buildNumber = props.getBuild();
        String versString = version != null ? version : YAWLEditor.DEFAULT_VERSION;
        if (buildNumber != null) {
            versString += " (build " + buildNumber + ")";
        }
        return versString;
    }

}
