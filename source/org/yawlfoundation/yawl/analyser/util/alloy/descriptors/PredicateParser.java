package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import java.util.HashMap;

public class PredicateParser {

    public PredicateParser() {

    }

    public String parse(String originalPredicate, HashMap<String, String> variables) {
        originalPredicate = originalPredicate.replaceAll("and", "&&");
        originalPredicate = originalPredicate.replaceAll("or", "||");
        for (String variable : variables.keySet()) {
            System.out.println(variable);
            System.out.println(originalPredicate);
            originalPredicate = originalPredicate.replaceAll(variable, String.format("s.%s", variable));
        }
        System.out.println(originalPredicate);
        System.out.println("-----------------------------------");
        return originalPredicate;
    }

    public String partialParse(String originalPredicate, HashMap<String, String> variables) {
        originalPredicate = originalPredicate.replaceAll("and", "&&");
        originalPredicate = originalPredicate.replaceAll("or", "||");
        return originalPredicate;
    }
}
