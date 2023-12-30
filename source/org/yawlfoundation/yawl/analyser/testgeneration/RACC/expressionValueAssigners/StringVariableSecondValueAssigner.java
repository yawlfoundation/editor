package org.yawlfoundation.yawl.analyser.testgeneration.RACC.expressionValueAssigners;

import org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase.EqualityOperator;
import org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase.VariableAssignment;

import java.util.ArrayList;

public class StringVariableSecondValueAssigner extends BaseExpressionValueAssigner {
    private final String secondOperand;
    private final String value;

    public StringVariableSecondValueAssigner(String value, String operand2, EqualityOperator operator) {
        super(operator);
        this.secondOperand = operand2;
        this.value = value;
    }

    public ArrayList<VariableAssignment> getAssignments() {
        ArrayList<VariableAssignment> result = new ArrayList<>();
        if (this.operator == EqualityOperator.equals) {
            result.add(new VariableAssignment(this.secondOperand, this.value));
            return result;
        }
        if (this.operator == EqualityOperator.notEquals || this.operator == EqualityOperator.greater || this.operator == EqualityOperator.greaterEquals) {
            result.add(new VariableAssignment(this.secondOperand, replaceFirstCharWithLowerChar(this.value)));
            return result;
        }
        String valueWithoutLastDoubleQuote = this.value.substring(0, this.value.length() - 2);
        result.add(new VariableAssignment(this.secondOperand, valueWithoutLastDoubleQuote.concat("A\"")));
        return result;
    }
}
