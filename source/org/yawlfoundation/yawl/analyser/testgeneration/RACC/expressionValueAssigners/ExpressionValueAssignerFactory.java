package org.yawlfoundation.yawl.analyser.testgeneration.RACC.expressionValueAssigners;

import org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase.ExpressionParser;

public class ExpressionValueAssignerFactory {
    public BaseExpressionValueAssigner
    getExpressionFirstValueAssigner(String value, String firstOperand,
                                    ExpressionParser expressionParser,
                                    boolean truthValue) {
        if (isInteger(value)) {
            return new IntegerVariableFirstValueAssigner(firstOperand, Integer.parseInt(value),
                    expressionParser.getEqualityOperator(truthValue));
        }
        return new StringVariableFirstValueAssigner(firstOperand, value,
                expressionParser.getEqualityOperator(truthValue));
    }

    public BaseExpressionValueAssigner
    getExpressionSecondValueAssigner(String value, String secondOperand,
                                     ExpressionParser expressionParser,
                                     boolean truthValue) {
        if (isInteger(value)) {
            return new IntegerVariableSecondValueAssigner(Integer.parseInt(value), secondOperand,
                    expressionParser.getEqualityOperator(truthValue));
        }
        return new StringVariableSecondValueAssigner(value, secondOperand,
                expressionParser.getEqualityOperator(truthValue));
    }

    public boolean isInteger(String operand) {
        System.out.println(operand);
        return !operand.matches("\".*\"");
    }
}
