package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.elements.YTask;

import java.util.List;

public abstract class OutputDescriptor extends TaskDescriptor{
    public OutputDescriptor(YTask taskNode, List<String> variables) {
        super(taskNode, variables);
    }

    public abstract String getOutputDescription();
}
