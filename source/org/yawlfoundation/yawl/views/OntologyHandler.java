package org.yawlfoundation.yawl.views;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
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


    public static ResultSet query(String query) {
        Query sparql = QueryFactory.create(query, Syntax.syntaxARQ);
        QueryExecution qExec = QueryExecutionFactory.create(sparql, _ontModel.getBaseModel());
        return qExec.execSelect();
    }

//    String query = "SELECT ?subject ?predicate ?object \n" +
//                       "WHERE { \n" +
//                       "?subject ?predicate ?object }";


//    while(rs.hasNext())
//        {
//            QuerySolution sol = rs.nextSolution();
//            RDFNode object = sol.get("object");
//            RDFNode predicate = sol.get("predicate");
//            RDFNode subject = sol.get("subject");
//
//            DefaultTableModel model = (DefaultTableModel) table.getModel();
//            model.addRow(new Object[]{subject, predicate, object});
//        }
}
