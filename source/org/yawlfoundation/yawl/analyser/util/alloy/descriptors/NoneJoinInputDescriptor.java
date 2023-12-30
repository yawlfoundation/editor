package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.elements.YTask;

import java.util.HashMap;

public class NoneJoinInputDescriptor extends InputDescriptor {
    public NoneJoinInputDescriptor(YTask taskNode, HashMap<String, String> variables, String toTransformOrJoin) {
        super(taskNode, variables, null);
    }

    @Override
    public String getInputDescription() {
        return "";
    }
}
