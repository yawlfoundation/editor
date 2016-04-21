package org.yawlfoundation.yawl.worklet.menu;

import org.yawlfoundation.yawl.editor.core.util.FileUtil;
import org.yawlfoundation.yawl.editor.ui.YAWLEditor;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.util.StringUtil;
import org.yawlfoundation.yawl.worklet.client.RdrSetID;
import org.yawlfoundation.yawl.worklet.client.WorkletClient;
import org.yawlfoundation.yawl.worklet.settings.SettingsStore;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class ExportRuleSetAction extends AbstractRuleSetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Export Rule Set");
        putValue(Action.NAME, "Export Rule Set");
        putValue(Action.LONG_DESCRIPTION, "Export a rule set to file");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    }


    @Override
    protected String getTitle() { return "Export Selected Rule Sets"; }


    @Override
    protected String getEmptyListMsg() {
        return "There are no stored rule sets to export";
    }


    @Override
    protected void processSelections(java.util.List<Object> selections) throws IOException {
        java.util.List<String> rdrSets = new ArrayList<String>();
        for (Object o : selections) {
            RdrSetID id = (RdrSetID) o;
            rdrSets.add(WorkletClient.getInstance().getRdrSet(id.getSpecID()));
        }
        File fExport = getSelectedPath(getSuggestedFileName(selections));
        if (fExport != null) {
            if (selections.size() > 1) {
                File tempDir = getTmpDir();
                for (int i=0; i < selections.size(); i++) {
                    String fileName = ((RdrSetID) selections.get(i)).getName() + ".xrs";
                    StringUtil.stringToFile(new File(tempDir, fileName), rdrSets.get(i));
                }
                FileUtil.zip(fExport, tempDir);
            }
            else {
                StringUtil.stringToFile(fExport, rdrSets.get(0));
            }
        }

        MessageDialog.info(selections.size() + " rule set" +
                        (selections.size() > 1 ? "s" : "") + " exported",
                "Export Selected Rule Sets");
    }


    private File getSelectedPath(String name) {
        File selected = null;
        if (name != null) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Rule Set");

            chooser.setSelectedFile(getInitialPath(name));
            int selection = chooser.showSaveDialog(YAWLEditor.getInstance());
            if (selection == JFileChooser.APPROVE_OPTION) {
                selected = chooser.getSelectedFile();
                savePath(selected);
            }
        }
        return selected;
    }


    private File getInitialPath(String name) {
        String lastPath = SettingsStore.getLastSaveFilePath();
        return lastPath != null ? new File(lastPath, name) : new File(name);
    }


    private void savePath(File file) {
        SettingsStore.setLastSaveFilePath(file.getParentFile().getAbsolutePath());
    }


    private String getSuggestedFileName(java.util.List<Object> selections) {
        if (! selections.isEmpty()) {
            String fileName = "ruleSets.zip";
            if (selections.size() == 1) {
                fileName = ((RdrSetID) selections.get(0)).getName() + ".xrs";
            }
            return fileName;
        }
        return null;
    }


    private File getTmpDir() {
        File dir = new File(System.getProperty("java.io.tmpdir"), "workletRules");
        dir.mkdirs();
        return dir;
    }

}
