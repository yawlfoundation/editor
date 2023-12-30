package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.elements.YTask;

import java.util.HashMap;

public abstract class InputDescriptor extends TaskDescriptor {
    public InputDescriptor(YTask taskNode, HashMap<String, String> variables, String toTransformOrJoin) {
        super(taskNode, variables, toTransformOrJoin);
    }

    public abstract String getInputDescription();

    public boolean isThisInputCondition() {
        return false;
    }
}
