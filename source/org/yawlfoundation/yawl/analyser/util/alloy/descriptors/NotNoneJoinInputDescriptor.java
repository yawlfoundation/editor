package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YInputCondition;
import org.yawlfoundation.yawl.elements.YTask;

import java.util.Iterator;
import java.util.List;

public class NotNoneJoinInputDescriptor extends InputDescriptor {

    public NotNoneJoinInputDescriptor(YTask taskNode, List<String> variables, String toTransformOrJoin) {
        super(taskNode, variables, toTransformOrJoin);
    }

    @Override
    public String getInputDescription() {
        clearStrBuilder();
        this.strBuilder.append("fact\n{all t: Object1 | (");
        String[] inputTasks = new String[this.taskNode.getPresetElements().size()];
        Iterator<YExternalNetElement> inputElementsIterator = this.taskNode.getPresetElements().iterator();
        for (int i = 0; i < this.taskNode.getPresetElements().size(); i++) {
            YExternalNetElement nextTask = inputElementsIterator.next();
            if (nextTask instanceof YInputCondition) {
                inputTasks[i] = (String.format("t = %s", "input_condition"));
            }
            else {
                inputTasks[i] = (String.format("t.label = \"%s\"", nextTask.getPresetElements().iterator().next().getName()));
            }
        }
        this.strBuilder.append(String.join(" || ", inputTasks));
        this.strBuilder.append(String.format("""
                        ) =>
                            {
                                one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "%s" && t2.join = "%s" && t2.split = "%s"
                            }
                        }
                                                
                        """,
                this.taskNode.getName(), this.getJoinGatewayTypeString(this.taskNode),
                this.getSplitGatewayTypeString(this.taskNode)));
        return this.strBuilder.toString();
    }
}
