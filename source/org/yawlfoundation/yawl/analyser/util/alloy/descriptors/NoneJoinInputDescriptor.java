package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.elements.YTask;

import java.util.List;

public class NoneJoinInputDescriptor extends InputDescriptor {
    public NoneJoinInputDescriptor(YTask taskNode, List<String> variables) {
        super(taskNode, variables);
    }

    @Override
    public String getInputDescription() {
        return "";
    }
}
