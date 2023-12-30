package org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase;

import java.util.ArrayList;
import java.util.HashMap;

public class ActiveClauseFinder {
    private final Clause _root;
    private final ArrayList<Clause> _leaves;

    public ActiveClauseFinder(Clause root) {
        this._root = root;
        this._leaves = new ArrayList<>();
        this._findLeaves(this._root);
    }

    public HashMap<String, ArrayList<HashMap<String, Boolean>>> find() {
        HashMap<String, ArrayList<HashMap<String, Boolean>>> result = new HashMap<>();
        ArrayList<HashMap<String, Boolean>> allPossibleAssignments = this.getAllPossibleAssignments(this._leaves);
        for (Clause leaf : this._leaves) {
            ArrayList<HashMap<String, Boolean>> allPossibleAssignmentsGivenVariableEqualsToTrue = this.replaceVariableWithValueInAssignment(allPossibleAssignments, leaf.getVirtualId(), true);
            ArrayList<HashMap<String, Boolean>> allPossibleAssignmentsGivenVariableEqualsToFalse = this.replaceVariableWithValueInAssignment(allPossibleAssignments, leaf.getVirtualId(), false);
            ArrayList<HashMap<String, Boolean>> variableResult = new ArrayList<>();
            for (int j = 0; j < allPossibleAssignmentsGivenVariableEqualsToTrue.size(); j++) {
                LogicalCalculusUtils solver = new LogicalCalculusUtils(this._root);
                boolean variableEqualsToTure = solver.solveClause(this._root, allPossibleAssignmentsGivenVariableEqualsToTrue.get(j));
                boolean variableEqualsToFalse = solver.solveClause(this._root, allPossibleAssignmentsGivenVariableEqualsToFalse.get(j));
                if (variableEqualsToTure ^ variableEqualsToFalse) {
                    variableResult.add(allPossibleAssignmentsGivenVariableEqualsToTrue.get(j));
                }
            }
            result.put(leaf.getVirtualId(), variableResult);
        }
        return result;
    }

    private ArrayList<HashMap<String, Boolean>> replaceVariableWithValueInAssignment(ArrayList<HashMap<String, Boolean>> allAssignments, String virtualId, boolean value) {
        ArrayList<HashMap<String, Boolean>> allNewPossibleAssignments = new ArrayList<>();
        for (HashMap<String, Boolean> assignment: allAssignments){
            if (assignment.get(virtualId) == value){
                allNewPossibleAssignments.add(assignment);
            }
        }
        return allNewPossibleAssignments;
    }

    private void _findLeaves(Clause clause) {
        if (clause.isLeaf()) {
            this._leaves.add(clause);
            return;
        }
        _findLeaves(clause.getChildren().get(0));
        _findLeaves(clause.getChildren().get(1));
    }

    public ArrayList<HashMap<String, Boolean>> getAllPossibleAssignments(ArrayList<Clause> leafClauses) {
        ArrayList<String> allVirtualIds = new ArrayList<>();
        ArrayList<HashMap<String, Boolean>> result = new ArrayList<>();
        for (Clause leafClause : leafClauses) {
            allVirtualIds.add(leafClause.getVirtualId());
        }
        for (int i = 0; i < Math.pow(2, leafClauses.size()); i++) {
            HashMap<String, Boolean> assignment = new HashMap<>();
            int[] binaryForm = toBinary(new int[leafClauses.size()], i);
            for (int j = 0; j < leafClauses.size(); j++) {
                if (binaryForm[j] == 0) {
                    assignment.put(allVirtualIds.get(j), false);
                } else if (binaryForm[j] == 1) {
                    assignment.put(allVirtualIds.get(j), true);
                }
            }
            result.add(assignment);
        }
        return result;
    }

    public static int[] toBinary(int[] binaryResult, int decimalNumber) {
        int index = 0;
        while (decimalNumber > 0) {
            binaryResult[index++] = decimalNumber % 2;
            decimalNumber = decimalNumber / 2;
        }
        return binaryResult;
    }

}
