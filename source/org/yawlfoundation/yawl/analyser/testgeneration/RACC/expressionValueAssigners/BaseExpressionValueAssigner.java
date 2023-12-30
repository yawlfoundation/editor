package org.yawlfoundation.yawl.analyser.testgeneration.RACC.expressionValueAssigners;

import org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase.EqualityOperator;
import org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase.VariableAssignment;

import java.util.ArrayList;

public abstract class BaseExpressionValueAssigner {
    protected final EqualityOperator operator;

    public BaseExpressionValueAssigner(EqualityOperator operator) {
        this.operator = operator;
    }

    protected static String replaceFirstCharWithLowerChar(String value) {
        char firstChar = value.charAt(0);
        char toBeReplacedWithFirstChar = (char) (firstChar - 1);
        StringBuilder stringBuilder = new StringBuilder(value);
        (stringBuilder).setCharAt(0, toBeReplacedWithFirstChar);
        return stringBuilder.toString();
    }

    public abstract ArrayList<VariableAssignment> getAssignments();
}
