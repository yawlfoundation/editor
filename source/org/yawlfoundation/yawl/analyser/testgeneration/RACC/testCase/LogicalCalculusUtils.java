package org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase;

import java.util.ArrayList;
import java.util.HashMap;

public class LogicalCalculusUtils {
    private final Clause _clause;

    public LogicalCalculusUtils(Clause root) {
        this._clause = root;
    }

    public Clause negative() {
        Clause clonedClause = this._clause.clone();
        return this._negative(clonedClause);
    }

    public boolean solveClause(Clause clause, HashMap<String, Boolean> assignments) {
        if (clause.isLeaf()) {
            if (clause.isNegative())
                return !assignments.get(clause.getVirtualId());
            return assignments.get(clause.getVirtualId());
        }
        if (clause.operator == LogicalOperator.or) {
            return solveClause(clause.getChildren().get(0), assignments) || solveClause(clause.getChildren().get(1), assignments);
        }
        return solveClause(clause.getChildren().get(0), assignments) && solveClause(clause.getChildren().get(1), assignments);
    }

    public Clause xor(Clause secondClause) {
        Clause firstClause = this._clause;
        Clause tempOrRoot = new Clause(null, null);
        tempOrRoot.operator = LogicalOperator.or;
        Clause tempAndLeftClause = new Clause(null, null);
        tempAndLeftClause.operator = LogicalOperator.and;
        tempOrRoot.addChild(tempAndLeftClause);
        Clause tempAndRightClause = new Clause(null, null);
        tempAndRightClause.operator = LogicalOperator.and;
        tempOrRoot.addChild(tempAndRightClause);
        Clause clonedFirstClause = firstClause.clone();
        Clause clonedSecondClause = secondClause.clone();
        Clause negativedClonedFirstClause = this._negative(firstClause.clone());
        Clause negativedClonedSecondClause = this._negative(secondClause.clone());
        tempAndLeftClause.addChild(negativedClonedFirstClause);
        tempAndLeftClause.addChild(clonedSecondClause);
        tempAndRightClause.addChild(clonedFirstClause);
        tempAndRightClause.addChild(negativedClonedSecondClause);
        return tempOrRoot;
    }

    private Clause _negative(Clause clause) {
        if (clause.isLeaf()) {
            clause.setNegative(!clause.isNegative());
            return clause;
        }
        clause.operator = negativeOperator(clause.operator);
        ArrayList<Clause> newChildren = new ArrayList<>();
        for (int i = 0; i < clause.getChildren().size(); i++) {
            newChildren.add(this._negative(clause.getChildren().get(i)));
        }
        clause.setChildren(newChildren);
        return clause;
    }

    public static LogicalOperator negativeOperator(LogicalOperator originalOperator) {
        if (originalOperator == LogicalOperator.or) {
            return LogicalOperator.and;
        }
        return LogicalOperator.or;
    }
}