package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import com.github.jsonldjava.utils.Obj;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YTask;

import java.util.List;

public class NotNoneJoinInputDescriptor extends InputDescriptor {

    public NotNoneJoinInputDescriptor(YTask taskNode, List<String> variables) {
        super(taskNode, variables);
    }

    @Override
    public String getInputDescription() {
        clearStrBuilder();
        this.strBuilder.append("""
                fact
                    {all t: task | (""");
        String[] inputTasks = new String[this.taskNode.getPresetFlows().size()];
        Object[] inputFlows = this.taskNode.getPresetFlows().toArray();
        for (int i = 0; i < this.taskNode.getPresetFlows().size(); i++) {
            inputTasks[i] = (String.format("t.label = \"%s\"", ((YFlow)inputFlows[i]).getPriorElement().getName()));
        }
        this.strBuilder.append(String.join(" || ", inputTasks));
        this.strBuilder.append(String.format("""
                ) =>\s
                {
                one t2: Object1 | t.flowsInto.nextTask = t2 &&\s
                t2.label = "%s" && t2.join = "%s" && t2.split = "%s"
                }
                }""", this.taskNode.getName(), this.taskNode.getJoinType(), this.taskNode.getSplitType()));
        return this.strBuilder.toString();
    }
}
