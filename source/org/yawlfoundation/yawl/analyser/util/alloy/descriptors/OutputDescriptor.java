package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YTask;

import java.util.HashMap;
import java.util.List;

public abstract class OutputDescriptor extends TaskDescriptor{
    public OutputDescriptor(YTask taskNode, HashMap<String, String> variables, String toTransformOrJoin) {
        super(taskNode, variables, toTransformOrJoin);
    }

    public abstract String getOutputDescription();

    protected String _getNextTaskDefinition(YExternalNetElement nextElement){
        if (this.isTaskOutputCondition(nextElement)){
            return " = output_condition";
        }
        if (nextElement instanceof YCondition condition){
            return String.format(".label = \"%s\"", condition.getPostsetElements().iterator().next().getName());
        }
        return nextElement.getName();
    }

    protected String getOutputTaskDescription(YTask outputTask) {
        String output = String.format(".label = \"%s\" && t1.split = \"%s\" && t1.join = \"%s\"",
                outputTask.getName(), this.getSplitGatewayTypeString(outputTask), this.getJoinGatewayTypeString(outputTask));
        if (outputTask.getCancelledBySet().size() > 0) {
            output = output + this._getCancellationRegionDescription(outputTask);
        }
        return output;
    }
    private String _getCancellationRegionDescription(YTask outputTask) {
        Object[] cancellationTasks = outputTask.getCancelledBySet().toArray();
        String[] cancelTasks = new String[cancellationTasks.length];
        for (int i = 0; i < cancellationTasks.length; i++) {
            cancelTasks[i] = String.format("c.label = %s", ((YExternalNetElement) cancellationTasks[i]).getName());
        }
        return String.format("\n && all c: task | (%s) => c in t.cancellation_region_objects",
                String.join(" || ", cancelTasks));
    }
}
