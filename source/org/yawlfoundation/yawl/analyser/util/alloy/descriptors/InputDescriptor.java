package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.elements.YTask;

import java.util.List;

public abstract class InputDescriptor extends TaskDescriptor {
    public InputDescriptor(YTask taskNode, List<String> variables) {
        super(taskNode, variables);
    }

    public abstract String getInputDescription();

    public boolean isThisInputCondition(){
        return false;
    }
}
