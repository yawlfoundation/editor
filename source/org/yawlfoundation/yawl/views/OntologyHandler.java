package org.yawlfoundation.yawl.views;


import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.yawlfoundation.yawl.editor.core.YSpecificationHandler;
import org.yawlfoundation.yawl.views.ontology.OntologyPopulator;
import org.yawlfoundation.yawl.views.ontology.SpecificationParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Adams
 * @date 13/10/2016
 */
public class OntologyHandler {

    private static final String BASE_ONT_FILE =
            "source/org/yawlfoundation/yawl/views/ontology/file/SpecificationOntology.owl";

    private static final String RULES_FILE_URL =
            "file:source/org/yawlfoundation/yawl/views/ontology/file/rules.txt";


    private static OntModel _ontModel;
    private static InfModel _infModel;

    private OntologyHandler() {
    }


    public static void unload() {
        _ontModel = null;
        _infModel = null;
    }


    public static void load(YSpecificationHandler handler) {
        System.out.println("==> Ontology Load Start");
        long start = System.currentTimeMillis();
        SpecificationParser parser = new SpecificationParser(handler);
        OntologyPopulator ontologyPopulator = new OntologyPopulator(BASE_ONT_FILE);
        ontologyPopulator.addIndividuals(parser);
        ontologyPopulator.addObjectProperties(parser);
        _ontModel = ontologyPopulator.getOntModel();
        _infModel = getInfModel(_ontModel);
        System.out.println("<== Ontology Load End: " +
                (System.currentTimeMillis() - start));
    }


    public static boolean isLoaded() {
        return _infModel != null;
    }

    public static void update(YSpecificationHandler handler) {
        if (isLoaded()) {
            load(handler);   // spec changed, so reload
        }
    }

    public static QueryResult query(String predStr) throws ViewsQueryException {
        return query(null, predStr, null);
    }


    public static QueryResult query(String sSubject, String sPredicate, String sObject)
            throws ViewsQueryException {
        if (_infModel == null) {
            throw new ViewsQueryException("No specification loaded.");
        }
        Resource subject = sSubject == null ? null :
                _infModel.getResource(OntologyPopulator.NAMESPACE + sSubject);
        Property predicate = sPredicate == null ? null :
                _infModel.getProperty(OntologyPopulator.NAMESPACE + sPredicate);
        RDFNode object = null;       // todo
        return query(subject, predicate, object);
    }


    private static InfModel getInfModel(OntModel model) {
        Reasoner reasoner = new GenericRuleReasoner(Rule.rulesFromURL(RULES_FILE_URL));
        InfModel infModel = ModelFactory.createInfModel(reasoner, model);
        infModel.prepare();
        return infModel;
    }


    private static QueryResult query(Resource subject, Property predicate, RDFNode object) {
        List<Statement> statements = new ArrayList<Statement>();
        StmtIterator it = _infModel.listStatements(subject, predicate, object);
        while (it.hasNext()) {
            statements.add(it.nextStatement());
        }
        return new QueryResult(statements);
    }

}
