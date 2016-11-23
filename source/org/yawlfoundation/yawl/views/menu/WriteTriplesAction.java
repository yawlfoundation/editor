package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;
import org.yawlfoundation.yawl.views.ontology.Triple;
import org.yawlfoundation.yawl.views.util.XLSWriter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Set;

class WriteTriplesAction extends YAWLSelectedNetAction {

    {
        putValue(Action.SHORT_DESCRIPTION, "Write Triples");
        putValue(Action.NAME, "Write Triples to File");
        putValue(Action.LONG_DESCRIPTION, "Write the populated ontology statements to disk");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_W);
    }


    public void actionPerformed(ActionEvent event) {
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
        Set<Triple> triples = OntologyHandler.swrlQuery();
        if (! triples.isEmpty()) {
            File f = getSelectedFilePath();
            long m = System.currentTimeMillis();
            if (f != null) {
                if (! f.getAbsolutePath().contains(".")) {
                    f = new File(f.getAbsolutePath() + ".xlsx");
                }
                if (write(f, triples)) {
                    System.out.println((System.currentTimeMillis() - m) + "mSecs");
                    MessageDialog.info(
                            triples.size() + " triples saved to Excel file",
                            "Write Triples");
                }
                else {
                    MessageDialog.error(
                            "Error writing to Excel file",
                            "Write Triples");
                }
            }
        }
        else {
            MessageDialog.warn("No triples returned from ontology",
                    "Write Triples");
        }
    }


    private File getSelectedFilePath() {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter(
                "Excel Workbook", "xlsx");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(filter);
        int option = fileChooser.showSaveDialog(null);
        return fileChooser.getSelectedFile();
    }


    private boolean write(File f, Set<Triple> triples) {
        XLSWriter writer = new XLSWriter();
        writer.writeHeader("SUBJECT", "PREDICATE", "OBJECT");
        for (Triple triple : triples) {
            writer.writeRow(triple);
        }
        writer.fixColumnWidths();
        return writer.output(f);
    }
}
