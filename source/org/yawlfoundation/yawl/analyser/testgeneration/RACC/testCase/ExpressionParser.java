package org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase;

import java.util.Map;

public class ExpressionParser {
    private static final Map<EqualityOperator, String> equalityOperatorSymbolMapping = Map.of(
            EqualityOperator.equals, " = ",
            EqualityOperator.notEquals, " != ",
            EqualityOperator.greater, " > ",
            EqualityOperator.greaterEquals, " >= ",
            EqualityOperator.smaller, " < ",
            EqualityOperator.smallerEquals, " <= "
    );

    private final String expression;

    public ExpressionParser(String expression) {
        this.expression = expression;
    }

    public String getFirstOperand() {
        EqualityOperator operator = this.getEqualityOperator(true);
        String[] operands = this.expression.split(equalityOperatorSymbolMapping.get(operator));
        return operands[0].stripTrailing();
    }

    public String getSecondOperand() {
        EqualityOperator operator = this.getEqualityOperator(true);
        String[] operands = this.expression.split(equalityOperatorSymbolMapping.get(operator));
//        System.out.printf("symbol is %s\n", equalityOperatorSymbolMapping.get(operator));
//        for (String operand : operands) {
//            System.out.printf("operand is %s\n", operand);
//        }
        return operands[1].stripLeading();
    }

    public EqualityOperator getEqualityOperator(boolean truthValue) {
        if (this.expression.contains("=") && !this.expression.contains(">")
                && !this.expression.contains("<") && !this.expression.contains("!"))
            return truthValue ? EqualityOperator.equals : EqualityOperator.notEquals;
        if (this.expression.contains("!="))
            return truthValue ? EqualityOperator.notEquals : EqualityOperator.equals;
        if (this.expression.contains("<="))
            return truthValue ? EqualityOperator.smallerEquals : EqualityOperator.greater;
        if (this.expression.contains(">="))
            return truthValue ? EqualityOperator.greaterEquals : EqualityOperator.smaller;
        if (this.expression.contains("<"))
            return truthValue ? EqualityOperator.smaller : EqualityOperator.greaterEquals;
        if (this.expression.contains(">"))
            return truthValue ? EqualityOperator.greater : EqualityOperator.smallerEquals;
        return EqualityOperator.equals;
    }

    public boolean isOperandVariable(String operand) {
        return operand.matches("[a-zA-Z]+") && !operand.startsWith("\"");
    }
}
