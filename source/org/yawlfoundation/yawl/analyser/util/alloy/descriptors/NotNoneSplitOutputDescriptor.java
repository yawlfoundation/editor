package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.apache.jena.base.Sys;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YTask;

import java.util.ArrayList;
import java.util.List;

public class NotNoneSplitOutputDescriptor extends OutputDescriptor {
    public NotNoneSplitOutputDescriptor(YTask taskNode, List<String> variables) {
        super(taskNode, variables);
    }

    @Override
    public String getOutputDescription() {
        clearStrBuilder();
        this.strBuilder.append(getTotalOutputTasksRelatedDescription());
        this.strBuilder.append(getPredicateConditionsDefinitions());
        return this.strBuilder.toString();
    }

    private String getPredicateConditionsDefinitions() {
        String[] predicateDescriptions = new String[this.taskNode.getPostsetFlows().size()];
        String currentTaskTitle = this.taskNode.getName();
        ArrayList<YFlow> outputFlows = new ArrayList<YFlow>(this.taskNode.getPostsetFlows());
        for (int i = 0; i < outputFlows.size(); i++) {
            String nextTaskTitle = outputFlows.get(i).getNextElement().getName();
            String originalPredicate = outputFlows.get(i).getXpathPredicate();
            predicateDescriptions[i] = String.format("""
                            fact {all s: State | all t: task | t.label = "%s" &&\s
                            \tt in s.token %s =>\t\t { one f:t.flowsInto | f.predicate.value = 1 && f.nextTask.label = "%s"}}
                             fact{
                            \tall s: State, s': s.next | all t: task, t':task | t in s.token && t.label = "%s" && t' in s'.token && t'.label = "%s"=> { one f: t.flowsInto | f.nextTask.label = "%s" && f.predicate.value = 1 %s}}""",
                    currentTaskTitle, getParsedPredicate(originalPredicate), nextTaskTitle, currentTaskTitle,
                    nextTaskTitle, nextTaskTitle, getParsedPredicate(originalPredicate));
        }
        return String.join("\n", predicateDescriptions);
    }

    private String getTotalOutputTasksRelatedDescription() {
        return String.format("""

                        fact{
                        all t: task | t.label = "%s" => {one %s: task | %s &&
                        all t%d: task | t%d in t.flowsInto.nextTask => %s}
                        }
                        """,
                this.taskNode.getName(), getOutputVariables(), getOutputTasksDescriptions(),
                this.taskNode.getPostsetFlows().size(), this.taskNode.getPostsetFlows().size(),
                getUtilityTaskDescription());
    }

    protected String getOutputTasksDescriptions() {
        String[] outputDescriptions = new String[this.taskNode.getPostsetFlows().size()];
        for (int idx = 0; idx < this.taskNode.getPostsetFlows().size(); idx++) {
            outputDescriptions[idx] = String.format("t%d in t.flowsInto.nextTask && %s", idx,
                    _getOutputTaskDescription(idx));
        }
        return String.join(" && \n", outputDescriptions);
    }

    private String _getOutputTaskDescription(int idx) {
        ArrayList<YFlow> outputFlows = new ArrayList<YFlow>(this.taskNode.getPostsetFlows());
        YExternalNetElement outputTask = outputFlows.get(idx).getNextElement();
        if (isTaskOutputCondition(outputTask)){
            return String.format("t%d = %s", idx, outputTask.getName());
        }
        String output = String.format("t%d.label = \"%s\"\n", idx, outputTask.getName());
        if (outputTask instanceof YTask outputYTask) {
            output += String.format(" && t%d.split = \"%s\" && t%d.join = \"%s\"", outputYTask.getSplitType(),
                    idx, outputYTask.getJoinType(), idx);
        }
        if (outputTask.getCancelledBySet().size() > 0) {
            output = output + this._getCancellationRegionDescription(outputTask, String.format("t%d", idx));
        }
        return output;
    }

    private String _getCancellationRegionDescription(YExternalNetElement outputTask, String outputTaskName) {
        ArrayList<YExternalNetElement> cancellationRegion = new ArrayList<>(outputTask.getCancelledBySet());
        String[] cancelTasks = new String[cancellationRegion.size()];
        for (int i = 0; i < outputTask.getCancelledBySet().size(); i++) {
            cancelTasks[i] = String.format("c.label = \"%s\"", cancellationRegion.get(i).getName());
        }
        return String.format("\n && all c: task | (%s) => c in %s.cancellation_region_objects",
                String.join(" || ", cancelTasks), outputTaskName);
    }
}
