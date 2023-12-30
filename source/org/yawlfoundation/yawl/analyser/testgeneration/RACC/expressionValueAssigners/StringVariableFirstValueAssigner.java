package org.yawlfoundation.yawl.analyser.testgeneration.RACC.expressionValueAssigners;

import org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase.EqualityOperator;
import org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase.VariableAssignment;

import java.util.ArrayList;

public class StringVariableFirstValueAssigner extends BaseExpressionValueAssigner {
    private final String firstOperand;
    private final String value;

    public StringVariableFirstValueAssigner(String operand1, String value, EqualityOperator operator) {
        super(operator);
        this.firstOperand = operand1;
        this.value = value;
    }

    public ArrayList<VariableAssignment> getAssignments() {
        ArrayList<VariableAssignment> result = new ArrayList<>();
        if (this.operator == EqualityOperator.equals) {
            result.add(new VariableAssignment(this.firstOperand, this.value));
            return result;
        }
        if (this.operator == EqualityOperator.notEquals || this.operator == EqualityOperator.greater || this.operator == EqualityOperator.greaterEquals) {
            System.out.println(this.value);
            String valueWithoutLastDoubleQuote = this.value.substring(0, this.value.length() - 2);
            result.add(new VariableAssignment(this.firstOperand, valueWithoutLastDoubleQuote.concat("A\"")));
            return result;
        }
        result.add(new VariableAssignment(this.firstOperand, replaceFirstCharWithLowerChar(this.value)));
        return result;
    }

}
