package org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase;


import org.yawlfoundation.yawl.analyser.testgeneration.RACC.exceptions.NotValidLeafClauseIdException;
import org.yawlfoundation.yawl.analyser.testgeneration.RACC.expressionValueAssigners.ExpressionValueAssignerFactory;
import org.yawlfoundation.yawl.analyser.testgeneration.RACC.expressionValueAssigners.IntegerVariableIntegerVariableValueAssigner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RACCTestCaseGenerator {
    private final ActiveClauseFinder activeClauseFinder;
    private final Clause rootClause;
    private final ArrayList<String> variableNames;

    public RACCTestCaseGenerator(Clause rootClause, ArrayList<String> variableNames) {
        this.rootClause = rootClause;
        this.activeClauseFinder = new ActiveClauseFinder(rootClause);
        this.variableNames = variableNames;
    }

    private static Clause findLeafClause(Clause clause, String variableName) {
        if (clause.isLeaf()) {
            if (clause.getVirtualId().equalsIgnoreCase(variableName)) {
                return clause;
            }
            return null;
        }
        ArrayList<Clause> children = clause.getChildren();
        Clause foundThroughLeftChild = findLeafClause(children.get(0), variableName);
        Clause foundThroughRightChild = findLeafClause(children.get(1), variableName);
        if (foundThroughRightChild != null) return foundThroughRightChild;
        return foundThroughLeftChild;
    }

    public HashMap<String, HashMap<Boolean, ArrayList<VariableAssignment>>> generate() throws NotValidLeafClauseIdException {
        HashMap<String, HashMap<Boolean, ArrayList<VariableAssignment>>> result = new HashMap<>();
        HashMap<String, ArrayList<HashMap<String, Boolean>>> activeClauses = this.activeClauseFinder.find();
        for (String toBeCoveredVariable : activeClauses.keySet()) {
            HashMap<Boolean, ArrayList<VariableAssignment>> variableCases = new HashMap<>();
            for (int i = 0; i < activeClauses.get(toBeCoveredVariable).size(); i++) {
                HashMap<String, Boolean> otherVariablesAssignments = activeClauses.get(toBeCoveredVariable).get(i);
                ArrayList<VariableAssignment> assignments = new ArrayList<>();
                for (String otherVariable : otherVariablesAssignments.keySet()) {
                    if (otherVariable.equals(toBeCoveredVariable)) continue;
                    boolean otherVariableValue = otherVariablesAssignments.get(otherVariable);
                    assignments.addAll(this.getTestCase(otherVariable, otherVariableValue));
                }
                ArrayList<VariableAssignment> testCaseWithToBeCoveredVariableTrue = new ArrayList<>(assignments);
                testCaseWithToBeCoveredVariableTrue.addAll(this.getTestCase(toBeCoveredVariable, true));
                ArrayList<VariableAssignment> testCaseWithToBeCoveredVariableFalse = new ArrayList<>(assignments);
                testCaseWithToBeCoveredVariableFalse.addAll(this.getTestCase(toBeCoveredVariable, false));
                variableCases.put(true, testCaseWithToBeCoveredVariableTrue);
                variableCases.put(false, testCaseWithToBeCoveredVariableFalse);
            }
            result.put(toBeCoveredVariable, variableCases);
        }
        return result;
    }

    private ArrayList<VariableAssignment> getTestCase(String variableName, boolean variableValue) {
        String expression = Objects.requireNonNull(findLeafClause(this.rootClause, variableName)).expression;
        return this.parseAndGenerateAssignment(expression, variableValue);
    }

    private ArrayList<VariableAssignment> parseAndGenerateAssignment(String expression, boolean truthValue) {
        if (expression == null || expression.isEmpty() || expression.isBlank())
            return new ArrayList<>();
        ExpressionParser expressionParser = new ExpressionParser(expression);
        String firstOperand = expressionParser.getFirstOperand();
        String secondOperand = expressionParser.getSecondOperand();
        System.out.println("-----------------------");
        System.out.println(firstOperand);
        System.out.println(secondOperand);
        System.out.println("-----------------------");
        if (isOperandVariable(firstOperand)) {
            if (isOperandVariable(secondOperand)) {
                return new IntegerVariableIntegerVariableValueAssigner(firstOperand,
                        secondOperand, expressionParser.getEqualityOperator(truthValue)).getAssignments();
            }
            return (new ExpressionValueAssignerFactory().getExpressionFirstValueAssigner(
                    secondOperand, firstOperand, expressionParser, truthValue)).getAssignments();
        }
        return (new ExpressionValueAssignerFactory().getExpressionSecondValueAssigner(
                firstOperand, secondOperand, expressionParser, truthValue)).getAssignments();

    }

    private boolean isOperandVariable(String operand) {
        System.out.println(operand);
        System.out.println(this.variableNames.contains(operand));
        return this.variableNames.contains(operand);
    }
}
