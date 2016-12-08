package org.yawlfoundation.yawl.views.dialog;

import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.lang.arq.ParseException;
import org.yawlfoundation.yawl.editor.ui.properties.data.validation.ExpressionMatcher;
import org.yawlfoundation.yawl.editor.ui.specification.SpecificationModel;
import org.yawlfoundation.yawl.views.ontology.OntologyHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Adams
 * @date 8/12/16
 */
public class RuleChecker {

    private final Map<String, String> _prefixMap;

    public RuleChecker(String prefixes) {
        _prefixMap = parsePrefixes(prefixes);
        if (!OntologyHandler.isLoaded()) {
            OntologyHandler.load(SpecificationModel.getHandler());
        }
    }


    public void parse(String rules) throws ParseException {
        String[] lines = rules.split("\n");
        for (int i=0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty() || "@#".contains(line.substring(0, 1))) {
                continue;
            }
            parseRule(line, i);
        }
    }


    private Map<String, String> parsePrefixes(String prefixStr) {
        Map<String, String> prefixMap = new HashMap<String, String>();
        String[] prefixArray = getLines(prefixStr);
        for (String prefixLine : prefixArray) {
            String[] parts = prefixLine.split("\\s+");
            if (parts[0].equals("@prefix")) {
                parts[2] = parts[2].substring(1, parts[2].lastIndexOf('#') +1);
                prefixMap.put(parts[1], parts[2]);
            }
        }
        return prefixMap;
    }


    private void parseRule(String rule, int lineNbr) throws ParseException {
        List<String> clauses =  new ExpressionMatcher("\\([^)]*\\)").getMatches(rule);
        for (String clause : clauses) {
            clause = deBrace(clause);
            String part[] = clause.split("[\\s\\p{Z}]");
            for (int i=0; i < part.length; i++) {
                if (!part[i].startsWith("?")) {
                    String literal = prepareLiteral(part[i]);
                    if (! hasObjectProperty(literal)) {
                        throw new ParseException(
                                String.format("Line %d, Column %d: Unknown object property '%s'",
                                        lineNbr + 1, clause.indexOf(part[i]) + 1, part[i]));
                    }
                }
            }
        }
    }


    private String prepareLiteral(String literal) {
        for (String prefix : _prefixMap.keySet()) {
            if (literal.startsWith(prefix)) {
                literal = literal.replaceFirst(prefix, _prefixMap.get(prefix));
                break;
            }
        }
        if (! literal.startsWith("<")) {
            literal = "<" + literal + ">";
        }
        return literal;
    }


    private boolean hasObjectProperty(String property) {
        String query = "select ?domain ?range where { " +
                property + " <http://www.w3.org/2000/01/rdf-schema#domain> " +
                "?domain ; <http://www.w3.org/2000/01/rdf-schema#range> ?range . }";
        ResultSet result = OntologyHandler.runSparqlQuery(query);
        return result != null && result.hasNext();
    }


    private String[] getLines(String s) { return s.split("\n"); }


    private String deBrace(String s) {
        s = s.trim();
        s = s.substring(1, s.length() -1);
        return s.trim();
    }
}
