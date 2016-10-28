package org.yawlfoundation.yawl.views.ontology;

import org.apache.jena.rdf.model.Statement;

import java.util.*;

/**
 * @author Michael Adams
 * @date 17/10/2016
 */
public class QueryResult {

    private List<Statement> _results;


    public QueryResult(List<Statement> results) {
        _results = results;
    }


    public Set<Triple> getTriples() {
        Set<Triple> triples = new HashSet<Triple>();
        for (Statement stmt : _results) {
            triples.add(new Triple(
                    removeNS(stmt.getSubject().toString()),
                    removeNS(stmt.getPredicate().toString()),
                    removeNS(stmt.getObject().toString())));
        }
        return triples;
    }


    public Map<String, Set<String>> getSubjectObjectPairs() {
        Map<String, Set<String>> pairs = new HashMap<String, Set<String>>();
        for (Statement stmt : _results) {
            String subject = removeNS(stmt.getSubject().toString());
            String object = removeNS(stmt.getObject().toString());
            Set<String> objectSet = pairs.get(subject);
            if (objectSet == null) {
                objectSet = new HashSet<String>();
                pairs.put(subject, objectSet);
            }
            objectSet.add(object);
        }
        return pairs;
    }


    public String getPredicate() {
        return _results.isEmpty() ? null :
                removeNS(_results.get(0).getPredicate().toString());
    }


    private String removeNS(String s) {
        return s.replace(OntologyPopulator.NAMESPACE, "");
    }

}
