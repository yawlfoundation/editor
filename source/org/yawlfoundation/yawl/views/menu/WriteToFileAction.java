package org.yawlfoundation.yawl.views.menu;

import org.yawlfoundation.yawl.editor.ui.actions.net.YAWLSelectedNetAction;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.editor.ui.swing.MessageDialog;
import org.yawlfoundation.yawl.util.StringUtil;
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

class WriteToFileAction extends YAWLSelectedNetAction {

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

        JFileChooser chooser = getSelectedFilePath();
        File f = chooser.getSelectedFile();
        if (f != null) {
            FileFilter filter = chooser.getFileFilter();
            if (filter.getDescription().equals("OWL")) {
                 writeToOWL(f);
            }
            else {
                Set<Triple> triples = OntologyHandler.sparqlQuery();
                if (!triples.isEmpty()) {
                    if (filter.getDescription().startsWith("Comma")) {
                        writeToCSV(f, triples);
                    }
                    else if (filter.getDescription().startsWith("Excel")) {
                        writeToExcel(f, triples);
                    }
                }
                else {
                    MessageDialog.warn("No triples returned from ontology",
                            "Write Triples");
                }
            }
        }
    }


    private JFileChooser getSelectedFilePath() {
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new FileNameExtensionFilter(
                        "Excel Workbook", "xlsx"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter(
                        "Comma Separated Values", "csv"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter(
                        "OWL", "owl"));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int option = chooser.showSaveDialog(null);
        return chooser;
    }


    private void writeToExcel(File f, Set<Triple> triples) {
        f = addExtension(f, ".xlsx");

        XLSWriter writer = new XLSWriter();
        writer.writeHeader("SUBJECT", "PREDICATE", "OBJECT");
        for (Triple triple : triples) {
            writer.writeRow(triple);
        }
        writer.fixColumnWidths();

        if (writer.output(f)) {
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


    private void writeToCSV(File f, Set<Triple> triples) {
        f = addExtension(f, ".csv");

        StringBuilder sb = new StringBuilder();
        sb.append("SUBJECT,PREDICATE,OBJECT\n");
        for (Triple triple : triples) {
            sb.append(triple.toCSV());
            sb.append('\n');
        }

        if (StringUtil.stringToFile(f, sb.toString()) != null) {
            MessageDialog.info(
                    triples.size() + " triples saved to CSV file",
                    "Write Triples");
        }
        else {
            MessageDialog.error(
                    "Error writing to CSV file",
                    "Write Triples");
        }
    }


    private void writeToOWL(File f) {
        f = addExtension(f, ".owl");
        if (OntologyHandler.save(f)) {
            MessageDialog.info("OWL file successfully saved to disk",
                    "OWL File Save");
        }
        else {
            MessageDialog.error("Failed to save OWL file to disk",
                    "OWL File Save");
        }
    }


    private File addExtension(File f, String extn) {
        return (f.getAbsolutePath().endsWith(extn)) ? f :
            new File(f.getAbsolutePath() + extn);
    }

}
