package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YOutputCondition;
import org.yawlfoundation.yawl.elements.YTask;

import java.util.HashMap;

public class NoneSplitOutputDescriptor extends OutputDescriptor {
    public NoneSplitOutputDescriptor(YTask taskNode, HashMap<String, String> variables, String toTransformOrJoin) {
        super(taskNode, variables, toTransformOrJoin);
    }

    @Override
    public String getOutputDescription() {
        if (isThisOutputCondition()) {
            return "";
        }
        YExternalNetElement outputElement = this.taskNode.getPostsetElements().iterator().next();
        System.out.println(this.taskNode.getName());
        System.out.println(outputElement.getName());
        System.out.println(outputElement.getClass());
        if (outputElement instanceof YOutputCondition outputCondition) {
            getOutputConditionOutputDescription(outputCondition);
        } else if (outputElement instanceof YCondition outputCondition) {
            addOutputOfRegularTask(outputCondition);
        }

        return this.strBuilder.toString();
    }

    private void addOutputOfRegularTask(YExternalNetElement outputElement) {
        YTask outputTask = (YTask) outputElement.getPostsetElements().iterator().next();
        this.strBuilder.append(String.format("""
                                        
                        fact {
                        all t: task | t.label = "%s" => {
                        one t1: task | t1 = t.flowsInto.nextTask && t1%s}}""", this.taskNode.getName(),
                this.getOutputTaskDescription(outputTask)));
    }

    private void getOutputConditionOutputDescription(YOutputCondition outputTask) {
        this.strBuilder.append(String.format("""
                                
                fact {
                all t: task | t.label = "%s" => {
                one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}""", this.taskNode.getName()));
    }
}
