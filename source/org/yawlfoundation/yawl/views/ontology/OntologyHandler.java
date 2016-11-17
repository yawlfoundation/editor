package org.yawlfoundation.yawl.views.ontology;


import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.yawlfoundation.yawl.editor.core.YSpecificationHandler;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Adams
 * @date 13/10/2016
 */
public class OntologyHandler {

    private static final String BASE_ONT_FILE =
            "source/org/yawlfoundation/yawl/views/ontology/file/SpecificationOntology.owl";

    private static final String RULES_FILE_URL =
            "file:source/org/yawlfoundation/yawl/views/ontology/file/rules.txt";


    private static List<Rule> _rules;            // rules from file
    private static OntModel _baseModel;          // only the ont file contents
    private static OntModel _populatedModel;     // above + spec individuals
    private static InfModel _infModel;           // above + rules & reasoner

    private OntologyHandler() { }


    public static void unload() {
        _populatedModel = null;
        _infModel = null;
    }


    public static void load(YSpecificationHandler handler) {
        SpecificationParser parser = new SpecificationParser(handler);
        OntologyPopulator ontologyPopulator = new OntologyPopulator(getBaseModel());
        _populatedModel = ontologyPopulator.populate(parser);
       _infModel = getInfModel(_populatedModel);
    }


    public static boolean isLoaded() {
        return _infModel != null;
    }


    public static void update(YSpecificationHandler handler) {
        load(handler);                                // spec changed, so reload
    }


    public static boolean save() {
        if (! isLoaded()) {
            load(SpecificationModel.getHandler());
        }
        try {
            new OntologyWriter().export(_populatedModel);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static QueryResult query(String predStr) throws OntologyQueryException {
        return query(null, predStr, null);
    }

    public static List<Triple> swrlQuery(String predStr) throws OntologyQueryException {
        return swrlQuery(null, predStr, null);
    }


    public static QueryResult query() {
        return getAllStatements();
    }

    public static Set<Triple> swrlQuery() {
        return getAllTriples();
    }


    public static QueryResult query(String sSubject, String sPredicate, String sObject)
            throws OntologyQueryException {
        if (_infModel == null) {
            throw new OntologyQueryException("No specification loaded.");
        }
        Resource subject = StringUtil.isNullOrEmpty(sSubject) ? null :
                _infModel.getResource(OntologyPopulator.NAMESPACE + sSubject);
        Property predicate = StringUtil.isNullOrEmpty(sPredicate) ? null :
                _infModel.getProperty(OntologyPopulator.NAMESPACE + sPredicate);
        RDFNode object = null;       // todo
        return query(subject, predicate, object);
    }


    public static List<Triple> swrlQuery(String sSubject, String sPredicate, String sObject)
            throws OntologyQueryException {
        if (_infModel == null) {
            throw new OntologyQueryException("No specification loaded.");
        }
        Resource subject = sSubject == null ? null :
                _infModel.getResource(OntologyPopulator.NAMESPACE + sSubject);
        Property predicate = sPredicate == null ? null :
                _infModel.getProperty(OntologyPopulator.NAMESPACE + sPredicate);
        RDFNode object = null;       // todo
        return swrlQuery(subject, predicate, object);
    }


    private static InfModel getInfModel(OntModel model) {
        Reasoner reasoner = new GenericRuleReasoner(getRules());
        InfModel infModel = ModelFactory.createInfModel(reasoner, model);
        infModel.prepare();
        return infModel;
    }


    private static List<Triple> swrlQuery(Resource subject, Property predicate, RDFNode object) {
        List<Triple> triples = new ArrayList<Triple>();

        String queryStr = "SELECT ?x ?y WHERE { ?x <" + predicate + "> ?y }";
        Query query = QueryFactory.create(queryStr);
        QueryExecution qexec = QueryExecutionFactory.create(query, _infModel);
        ResultSet results = qexec.execSelect();
        for (; results.hasNext(); ) {
            QuerySolution soln = results.nextSolution();
            String s = soln.getResource("?x").getLocalName();
            String o = soln.getResource("?y").getLocalName();
            triples.add(new Triple(s, predicate.toString(), o));
        }
        return triples;
    }


    private static Set<Triple> getAllTriples() {
        Set<Triple> triples = new HashSet<Triple>();

        String queryStr = "SELECT DISTINCT ?x ?p ?y WHERE { ?x ?p ?y }";
        Query query = QueryFactory.create(queryStr);
        QueryExecution qexec = QueryExecutionFactory.create(query, _infModel);
        ResultSet results = qexec.execSelect();

        for (; results.hasNext(); ) {
            QuerySolution soln = results.nextSolution();
            String s = soln.getResource("?x").getLocalName();
            String p = soln.getResource("?p").getLocalName();
            String o = soln.getResource("?y").getLocalName();
            Triple t = new Triple(s, p, o);
            triples.add(t);
        }
        return triples;
    }


    private static QueryResult query(Resource subject, Property predicate, RDFNode object) {
        List<Statement> statements = new ArrayList<Statement>();
        StmtIterator it = _infModel.listStatements(subject, predicate, object);
        while (it.hasNext()) {
            Statement s = it.nextStatement();
            statements.add(s);
        }
        return new QueryResult(statements);
    }


    private static QueryResult getAllStatements() {
        List<Statement> statements = new ArrayList<Statement>();
        StmtIterator it = _populatedModel.listStatements();
        while (it.hasNext()) {
            statements.add(it.nextStatement());
        }
        return new QueryResult(statements);
    }


    private static OntModel getBaseModel() {
//        if (_baseModel == null) {
            _baseModel = new OntologyReader().read(BASE_ONT_FILE);
//        }
        return _baseModel;
    }


    private static List<Rule> getRules() {
        if (_rules == null) {
//            GenericRuleReasoner rdfsReasoner =
//                    (GenericRuleReasoner) ReasonerRegistry.getRDFSReasoner();
//            _rules = new ArrayList<Rule>(rdfsReasoner.getRules());
//            _rules.addAll(Rule.rulesFromURL(RULES_FILE_URL));
            _rules = Rule.rulesFromURL(RULES_FILE_URL);
        }
        return _rules;
    }

}
