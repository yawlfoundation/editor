package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import java.util.List;

public class PredicateParser {

    public PredicateParser() {

    }

    public String parse(String originalPredicate, List<String> variables) {
        originalPredicate = originalPredicate.replaceAll("and", "&&");
        originalPredicate = originalPredicate.replaceAll("or", "||");
        for (String variable: variables) {
            originalPredicate = originalPredicate.replaceAll(variable, String.format("s.%s", variable));
        }
        return originalPredicate;
    }
}
