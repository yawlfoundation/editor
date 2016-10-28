package org.yawlfoundation.yawl.views.ontology;


import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.security.NoSuchAlgorithmException;

/**
 * @author Michael Adams
 *         - prototype by Gary Grossgarten (h-brs.de)
 *         <p>
 *         Test class (manually loads and saves files)
 */

public class Main {


    public static void main(String[] args) {
        try {
            SpecificationParser specificationParser = loadSpecification();
            if (specificationParser != null) {
                exportAsOwl(populateOntology(specificationParser));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static OntologyPopulator populateOntology(SpecificationParser specificationParser) {
        OntologyPopulator ontologyPopulator = new OntologyPopulator(
                "source/org/yawlfoundation/yawl/views/ontology/file/SpecificationOntology.owl");
        ontologyPopulator.addIndividuals(specificationParser);
        ontologyPopulator.addObjectProperties(specificationParser);
        return ontologyPopulator;
    }


    private static void exportAsOwl(final OntologyPopulator ontologyPopulator) throws IOException, NoSuchAlgorithmException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String path = getSelectedFilePath("owl", true);
                if (path != null) {
                    try {
                        Writer writer = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(path), "UTF-8"));
                        ontologyPopulator.outputTo(writer);
                        writer.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private static SpecificationParser loadSpecification() throws IOException, NoSuchAlgorithmException {
        String path = getSelectedFilePath("yawl", false);
        return path != null ? new SpecificationParser(path) : null;
    }


    private static String getSelectedFilePath(String filterExtn, boolean save) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter(
                filterExtn.toUpperCase(), filterExtn);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(filter);
        int option = save ? fileChooser.showSaveDialog(null) :
                fileChooser.showOpenDialog(null);

        return option == JFileChooser.APPROVE_OPTION ?
                fileChooser.getSelectedFile().getAbsolutePath() : null;
    }


}
