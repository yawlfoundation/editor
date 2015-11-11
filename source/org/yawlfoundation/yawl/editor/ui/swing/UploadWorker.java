package org.yawlfoundation.yawl.editor.ui.swing;

import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.specification.io.SpecificationUploader;
import org.yawlfoundation.yawl.editor.ui.util.CursorUtil;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.util.XNode;
import org.yawlfoundation.yawl.util.XNodeParser;

import javax.swing.*;
import java.io.IOException;

/**
 * @author Michael Adams
 * @date 11/11/2015
 */
public class UploadWorker extends SwingWorker<Void, Void> {

    private String _message;
    private String _title;
    private int _msgType = JOptionPane.INFORMATION_MESSAGE;

    private final boolean _unloadPrevious;
    private final boolean _cancelPrevious;
    private final boolean _launchCase;


    public UploadWorker(boolean unloadPrevious, boolean cancelPrevious, boolean launchCase) {
        super();
        _unloadPrevious = unloadPrevious;
        _cancelPrevious = cancelPrevious;
        _launchCase = launchCase;
    }


    @Override
    protected Void doInBackground() throws Exception {
        SpecificationUploader uploader = new SpecificationUploader();
        try {
            String result = uploader.upload(_unloadPrevious, _cancelPrevious);
            String errorMsg = processUploadResult(result);
            if (errorMsg.isEmpty()) {
                uploader.storeLayout();
                _message = "Specification uploaded successfully.";
                if (_launchCase) {
                    _message += launchCase(uploader);
                }
            }
            else {
                _message = errorMsg;
                _msgType = JOptionPane.ERROR_MESSAGE;
            }
        }
        catch (IOException ioe) {
            _msgType = JOptionPane.ERROR_MESSAGE;
            _message = unwrap(ioe.getMessage());
            if (_message.equals("Invalid Specification")) {
                _message += ". Please resolve the issues listed in the " +
                        "\n'Validation Results' pane below before retrying an upload.";
            }
        }
        _title = "Upload " + (_msgType == JOptionPane.ERROR_MESSAGE ?
                "Error" : "Success");
        return null;
    }

    @Override
    protected void done() {
        CursorUtil.showDefaultCursor();
        JOptionPane.showMessageDialog(YAWLEditor.getInstance(), _message, _title, _msgType);
    }


    private String launchCase(SpecificationUploader uploader) throws IOException {
        String caseID = uploader.launchCase();
        if (caseID != null) {
            return caseID.contains("fail") ?
                    "\n\nCase launch failed: " + StringUtil.unwrap(caseID) :
                    "\n\nNew case launched with id: " + caseID;
        }
        return "";
    }


    private String processUploadResult(String result) {
        if (result.contains("fail")) {
            XNode msgNode = new XNodeParser().parse(result);
            String errMsg = msgNode.getText();

            // simple error message - return immediately
            return errMsg != null ? unwrap(errMsg) :
                    processUploadValidationResult(msgNode);
        }
        return "";                                 // no errors
    }


    private String processUploadValidationResult(XNode msgNode) {
        StringBuilder s = new StringBuilder();
        XNode reason = msgNode.getChild();
        XNode messages = reason.getChild();

        // only interested in errors
        for (XNode errorNode : messages.getChildren("error")) {
            XNode message = errorNode.getChild("message");
            s.append(message.getText()).append('\n');
        }
        return s.toString();
    }


    private String unwrap(String xml) {
        return xml.startsWith("<") ? StringUtil.unwrap(xml) : xml;
    }
}


