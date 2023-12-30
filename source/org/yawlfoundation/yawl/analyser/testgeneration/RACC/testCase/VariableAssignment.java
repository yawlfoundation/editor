package org.yawlfoundation.yawl.analyser.testgeneration.RACC.testCase;

public class VariableAssignment {
    private final String name;
    private final String value;

    public VariableAssignment(String name, String value) {
        this.name = name;
        this.value = value;

    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s = %s", this.name, this.value);
    }
}
