package org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase;

public class BinaryTreeLogicSolver {
    private final Clause _root;

    public BinaryTreeLogicSolver(Clause root) {
        this._root = root;
    }

    public boolean Solve() {
        return this._Solve(this._root);
    }

    private boolean _Solve(Clause clause) {
        if (clause.isLeaf()) {
            return clause.getValue();
        }
        if (clause.operator == LogicalOperator.or) {
            return (_Solve(clause.getChildren().get(0)) || _Solve(clause.getChildren().get(1)));
        }
        return (_Solve(clause.getChildren().get(0)) && _Solve(clause.getChildren().get(1)));
    }
}
