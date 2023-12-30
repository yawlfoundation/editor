package org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase;

public class PredicateToClauseStructureConvertor {
    private int startVirtualIdsAlphabetAsciiIndex = 65;

    public Clause convert(String predicate) {
        predicate = predicate.replaceAll("^\\(", "");
        predicate = predicate.replaceAll("\\)$", "");
        if (!predicate.contains("(") && !predicate.contains(")")) {
            return new Clause(predicate, String.valueOf((char) startVirtualIdsAlphabetAsciiIndex++));
        }
        LogicalOperator operator = LogicalOperator.or;
        int firstClauseLastCharacterIdx = 0;
        int openedParanthesisCount = 0;
        for (int i = 0; i < predicate.length(); i++) {
            if (predicate.charAt(i) == '(') {
                openedParanthesisCount++;
                continue;
            }
            if (predicate.charAt(i) == ')') {
                openedParanthesisCount--;
                continue;
            }
            if (predicate.charAt(i) == '&' && predicate.charAt(i + 1) == '&' && openedParanthesisCount == 0) {
                firstClauseLastCharacterIdx = i - 1;
                operator = LogicalOperator.and;
                break;
            }
            if (predicate.charAt(i) == '|' && predicate.charAt(i + 1) == '|' && openedParanthesisCount == 0) {
                firstClauseLastCharacterIdx = i - 1;
                operator = LogicalOperator.or;
                break;
            }
        }
        Clause clause = new Clause(null, null);
        clause.setOperator(operator);
        String firstClause = predicate.substring(0, firstClauseLastCharacterIdx);
        String secondClause = predicate.substring(firstClauseLastCharacterIdx + 4);
        Clause firstChild = convert(firstClause);
        firstChild.setParent(clause);
        Clause secondChild = convert(secondClause);
        secondChild.setParent(clause);
        clause.addChild(firstChild);
        clause.addChild(secondChild);
        return clause;
    }
}
