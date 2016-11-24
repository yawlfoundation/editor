package org.yawlfoundation.yawl.views.ontology;

import org.apache.jena.ontology.OntModel;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.security.NoSuchAlgorithmException;

/**
 * @author Michael Adams
 * @date 8/11/16
 */
public class OntologyWriter {


    public void export(final OntModel model) throws IOException, NoSuchAlgorithmException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String path = getSelectedFilePath("owl");
                if (path != null) {
                    try {
                        Writer writer = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(path), "UTF-8"));
                        model.getBaseModel().write(writer);
                        writer.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private String getSelectedFilePath(String filterExtn) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter(
                filterExtn.toUpperCase(), filterExtn);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(filter);
        int option = fileChooser.showSaveDialog(null);

        return option == JFileChooser.APPROVE_OPTION ?
                fileChooser.getSelectedFile().getAbsolutePath() : null;
    }

}
