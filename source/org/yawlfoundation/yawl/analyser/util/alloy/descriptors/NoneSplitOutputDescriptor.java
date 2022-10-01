package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.apache.jena.base.Sys;
import org.yawlfoundation.yawl.elements.*;

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
        YExternalNetElement outputElement = this.taskNode.getPostsetElements().iterator().next();
        if (outputElement instanceof YOutputCondition outputCondition) {
            getOutputConditionOutputDescription(outputCondition);
        } else if (outputElement instanceof YTask outputTask) {
            addOutputOfRegularTask(outputTask);
        }

        return this.strBuilder.toString();
    }

    private void addOutputOfRegularTask(YTask outputTask) {
        this.strBuilder.append(String.format("""
                                
                fact {
                all t: task | t.label = "%s" => {
                one t1: task | t1 = t.flowsInto.nextTask && t1%s}}""", this.taskNode.getName(), this.getOutputTaskDescription((YTask) this.taskNode.getPostsetFlows().toArray()[0])));
    }

    private void getOutputConditionOutputDescription(YOutputCondition outputTask) {
        this.strBuilder.append(String.format("""
                                
                fact {
                all t: task | t.label = "%s" => {
                one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}""", this.taskNode.getName()));
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
