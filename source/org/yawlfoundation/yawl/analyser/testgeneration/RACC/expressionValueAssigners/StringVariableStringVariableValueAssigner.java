package org.yawlfoundation.yawl.analyser.testgeneration.RACC.expressionValueAssigners;

import org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase.EqualityOperator;
import org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase.VariableAssignment;

import java.util.ArrayList;

public class StringVariableStringVariableValueAssigner extends BaseExpressionValueAssigner {
    private final String firstOperand;
    private final String secondOperand;

    public StringVariableStringVariableValueAssigner(String operand1, String operand2, EqualityOperator operator) {
        super(operator);
        this.firstOperand = operand1;
        this.secondOperand = operand2;
    }

    public ArrayList<VariableAssignment> getAssignments() {
        ArrayList<VariableAssignment> result = new ArrayList<>();
        if (this.operator == EqualityOperator.equals) {
            result.add(new VariableAssignment(this.firstOperand, "a"));
            result.add(new VariableAssignment(this.secondOperand, "a"));
            return result;
        }
        if (this.operator == EqualityOperator.notEquals) {
            result.add(new VariableAssignment(this.firstOperand, "a"));
            result.add(new VariableAssignment(this.secondOperand, "b"));
            return result;
        }
        if (this.operator == EqualityOperator.greater) {
            result.add(new VariableAssignment(this.firstOperand, "c"));
            result.add(new VariableAssignment(this.secondOperand, "b"));
            return result;
        }
        if (this.operator == EqualityOperator.greaterEquals) {
            result.add(new VariableAssignment(this.firstOperand, "c"));
            result.add(new VariableAssignment(this.secondOperand, "a"));
            return result;
        }
        if (this.operator == EqualityOperator.smaller) {
            result.add(new VariableAssignment(this.firstOperand, "a"));
            result.add(new VariableAssignment(this.secondOperand, "b"));
            return result;
        }
        result.add(new VariableAssignment(this.firstOperand, "a"));
        result.add(new VariableAssignment(this.secondOperand, "c"));
        return result;
    }
}
