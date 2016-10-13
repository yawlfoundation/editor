package org.yawlfoundation.yawl.views;

import org.apache.jena.ontology.OntModel;
import org.yawlfoundation.yawl.editor.core.YSpecificationHandler;
import org.yawlfoundation.yawl.views.ontology.OntologyPopulator;
import org.yawlfoundation.yawl.views.ontology.SpecificationParser;

/**
 * @author Michael Adams
 * @date 13/10/2016
 */
public class OntologyHandler {

    private static final String BASE_ONT_FILE =
            "source/org/yawlfoundation/yawl/views/ontology/file/SpecificationOntology.owl";

    private static OntModel _ontModel;

    private OntologyHandler() {
    }


    public static void unload() {
        _ontModel = null;
    }


    public static void load(YSpecificationHandler handler) {
        SpecificationParser parser = new SpecificationParser(handler);
        OntologyPopulator ontologyPopulator = new OntologyPopulator(BASE_ONT_FILE);
        ontologyPopulator.addIndividuals(parser);
        ontologyPopulator.addObjectProperties(parser);
        _ontModel = ontologyPopulator.getOntModel();
    }

}
