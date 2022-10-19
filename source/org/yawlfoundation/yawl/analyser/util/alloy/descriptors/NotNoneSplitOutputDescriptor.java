package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.apache.jena.base.Sys;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotNoneSplitOutputDescriptor extends OutputDescriptor {
    public NotNoneSplitOutputDescriptor(YTask taskNode, List<String> variables, String toTransformOrJoin) {
        super(taskNode, variables, toTransformOrJoin);
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
        Iterator<YFlow> outputFlowsIterator = this.taskNode.getPostsetFlows().iterator();
        for (int i = 0; i < this.taskNode.getPostsetFlows().size(); i++) {
            YFlow currentFlow = outputFlowsIterator.next();
            String originalPredicate = currentFlow.getXpathPredicate();
            String nextTaskDefinition = this._getNextTaskDefinition(currentFlow.getNextElement());
            predicateDescriptions[i] = String.format("""
     
                             fact{
                            \tall s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "%s" && t' in s'.token &&
                            t'%s => { one f: t.flowsInto | f.nextTask%s && f.predicate.value = 1 %s}
                            }
                            """,
                    currentTaskTitle, nextTaskDefinition, nextTaskDefinition, getParsedPredicate(originalPredicate));
        }
        return String.join("\n", predicateDescriptions);
    }

    private String getTotalOutputTasksRelatedDescription() {
        return String.format("""

                        fact{
                            all t: task | t.label = "%s" => {
                            %s
                            }
                        }
                        """,
                this.taskNode.getName(), getOutputTasksDescriptions());
    }

    protected String getOutputTasksDescriptions() {
        String[] outputDescriptions = new String[this.taskNode.getPostsetElements().size()];
        Iterator<YExternalNetElement> outputElementsIterator = this.taskNode.getPostsetElements().iterator();
        for (int idx = 0; idx < this.taskNode.getPostsetElements().size(); idx++) {
            outputDescriptions[idx] = String.format("{ one t%d: Object1 | t%d in t.flowsInto.nextTask && %s }",
                    idx, idx, _getOutputTaskDescription(outputElementsIterator.next(), idx));
        }
        return String.join(" && \n", outputDescriptions);
    }

    private String _getOutputTaskDescription(YExternalNetElement outputElement, int idx) {
        if (isTaskOutputCondition(outputElement)){
            return String.format("t%d = output_condition", idx);
        }
        YTask outputTask = (YTask) outputElement.getPostsetElements().iterator().next();
        String output = String.format("t%d.label = \"%s\"\n", idx, outputTask.getName());
        output += String.format(" && t%d.split = \"%s\" && t%d.join = \"%s\"",
                idx, this.getSplitGatewayTypeString(outputTask),
                idx, this.getJoinGatewayTypeString(outputTask));
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
