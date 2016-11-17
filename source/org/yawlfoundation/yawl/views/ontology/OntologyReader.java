package org.yawlfoundation.yawl.views.ontology;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

/**
 * @author Michael Adams
 * @date 8/11/16
 */
public class OntologyReader {

    /**
     * loads the ontology from a given path and initializes the
     * Classes and Object Properties of the respective ontology
     * for further actions
     */
    public OntModel read(String ontoFile) {
        // OntModelSpec.OWL_MEM_MINI_RULE_INF
        // PelletReasonerFactory.THE_SPEC
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        InputStream in = FileManager.get().open(ontoFile);
        try {
            ontModel.read(in, null);
            return ontModel;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
