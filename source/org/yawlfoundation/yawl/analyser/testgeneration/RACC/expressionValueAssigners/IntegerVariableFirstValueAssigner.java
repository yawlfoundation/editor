package org.yawlfoundation.yawl.analyser.testgeneration.RACC.expressionValueAssigners;


import org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase.EqualityOperator;
import org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase.VariableAssignment;

import java.util.ArrayList;

public class IntegerVariableFirstValueAssigner extends BaseExpressionValueAssigner {
    private final String firstOperand;
    private final int value;

    public IntegerVariableFirstValueAssigner(String operand1, int value, EqualityOperator operator) {
        super(operator);
        this.firstOperand = operand1;
        this.value = value;
    }

    public ArrayList<VariableAssignment> getAssignments() {
        ArrayList<VariableAssignment> result = new ArrayList<>();
        if (this.operator == EqualityOperator.equals) {
            result.add(new VariableAssignment(this.firstOperand, Integer.toString(this.value)));
            return result;
        }
        if (this.operator == EqualityOperator.notEquals || this.operator == EqualityOperator.greater || this.operator == EqualityOperator.greaterEquals) {
            result.add(new VariableAssignment(this.firstOperand, Integer.toString(this.value + 1)));
            return result;
        }
        result.add(new VariableAssignment(this.firstOperand, Integer.toString(this.value - 1)));
        return result;
    }
}
