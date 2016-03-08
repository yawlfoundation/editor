package org.yawlfoundation.yawl.editor.ui.properties.data;

/**
 * @author Michael Adams
 * @date 8/03/2016
 */
public enum VariableScope {

    LOCAL("Local", -1),
    INPUT("Input", 0),
    OUTPUT("Output", 1),
    INPUT_OUTPUT("InputOutput", 2);


    String _label;
    int _value;

    VariableScope(String label, int value) {
        _label = label;
        _value = value;
    }

    public String getLabel() { return _label; }

    public int getValue() { return _value; }

    public static VariableScope getScope(int value) {
        for (VariableScope scope : values()) {
            if (scope.getValue() == value) {
                return scope;
            }
        }
        return INPUT;   // should never reach this
    }

}
