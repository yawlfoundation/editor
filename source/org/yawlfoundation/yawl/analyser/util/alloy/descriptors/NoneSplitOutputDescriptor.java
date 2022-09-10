package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YTask;

import java.util.List;

public class NoneSplitOutputDescriptor extends OutputDescriptor {
    public NoneSplitOutputDescriptor(YTask taskNode, List<String> variables) {
        super(taskNode, variables);
    }

    @Override
    public String getOutputDescription() {
        if (isThisOutputCondition()) {
            return "";
        }
        YTask outputTask = (YTask) this.taskNode.getPostsetFlows().toArray()[0];
        if (isThisInputCondition()) {
            addOutputOfInputCondition(outputTask);
        } else {
            addOutputOfRegularTask(outputTask);
        }

        return this.strBuilder.toString();
    }

    private void addOutputOfRegularTask(YTask outputTask) {
        this.strBuilder.append(String.format("\nfact " +
                "{\nall t: task | t.label = \"%s\" => {" +
                "\none t1: task | t1 = t.flowsInto.nextTask && t1%s" +
                "}" +
                "}", this.taskNode.getName(), this.getOutputTaskDescription((YTask) this.taskNode.getPostsetFlows().toArray()[0])));
    }

    private void addOutputOfInputCondition(YTask outputTask) {
        this.strBuilder.append(String.format("fact\n{one t1: task | t1 = %s.flowsInto.nextTask && t1%s}",
                this.taskNode.getName(), this.getOutputTaskDescription((YTask) this.taskNode.getPostsetFlows().toArray()[0])));
    }

    protected String getOutputTaskDescription(YTask outputTask) {
        if (((YTask) this.taskNode.getPostsetFlows().toArray()[0]).getName().equals("OutputCondition"))
            return String.format(" = %s", ((YTask) this.taskNode.getPostsetFlows().toArray()[0]).getName());
        String output = String.format(".label = \"%s\" && t1.split = \"%s\" && t1.join = \"%s\"",
                outputTask.getName(), outputTask.getSplitType(), outputTask.getJoinType());
        if (outputTask.getCancelledBySet().size() > 0) {
            output = output + this._getCancellationRegionDescription(outputTask);
        }
        return output;
    }

    private String _getCancellationRegionDescription(YTask outputTask) {
        Object[] cancellationTasks = outputTask.getCancelledBySet().toArray();
        String[] cancelTasks = new String[cancellationTasks.length];
        for (int i = 0; i < cancellationTasks.length; i++) {
            cancelTasks[i] = String.format("c.label = %s", ((YExternalNetElement)cancellationTasks[i]).getName());
        }
        return String.format("\n && all c: task | (%s) => c in t.cancellation_region_objects",
                String.join(" || ", cancelTasks));
    }
}
