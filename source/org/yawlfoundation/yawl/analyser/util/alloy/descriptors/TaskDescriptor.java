package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.analyser.util.alloy.utils.DescriptionUtil;
import org.yawlfoundation.yawl.analyser.util.alloy.utils.GatewayType;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YTask;

import java.util.List;

public abstract class TaskDescriptor {
    protected final YTask taskNode;
    protected final PredicateParser _predicateParser;
    protected StringBuilder strBuilder;
    protected List<String> variables;

    public TaskDescriptor(YTask taskNode, List<String> variables) {
        this.taskNode = taskNode;
        this.strBuilder = new StringBuilder();
        this._predicateParser = new PredicateParser();
        this.variables = variables;
    }

    protected void clearStrBuilder() {
        this.strBuilder.delete(0, this.strBuilder.length());
    }

    protected boolean isThisInputCondition() {
        return this.taskNode.getName().equals("inputCondition");
    }

    protected boolean isThisOutputCondition() {
        return this.taskNode.getName().equals("OutputCondition");
    }

    protected String getOutputVariables() {
        String[] variables = new String[this.taskNode.getPostsetFlows().size()];
        for (int i = 0; i < this.taskNode.getPostsetFlows().size(); i++) {
            variables[i] = "t" + i;
        }
        return String.join(", ", variables);
    }

    protected String getUtilityTaskDescription() {
        String[] utilityDescriptions = new String[this.taskNode.getPostsetFlows().size()];
        String utilityIdxStr = Integer.toString(this.taskNode.getPostsetFlows().size());
        for (int i = 0; i < this.taskNode.getPostsetFlows().size(); i++) {
            utilityDescriptions[i] = String.format("t%s = t%d", utilityIdxStr, i);
        }
        return String.join(" || ", utilityDescriptions);
    }

    protected String getParsedPredicate(String predicate) {
        if (predicate != null && !predicate.equals("") && !predicate.equals("true()") && !predicate.equals("false()"))
            return String.format("&& %s", this._predicateParser.parse(predicate, this.variables));
        return "";
    }

    protected boolean isTaskOutputCondition(YExternalNetElement outputTask) {
        if (outputTask.getID() != null) {
            return outputTask.getID().equals("outputCondition");
        }
        return false;
    }

    protected String getSplitGatewayTypeString(YExternalNetElement netElement) {
        if (netElement instanceof YTask task) {
            if (task.getPostsetElements().size() > 1) {
                return toCamelCase(DescriptionUtil.getGatewayType(task.getSplitType()).toString());
            }
        }
        return toCamelCase(GatewayType.None.toString());
    }
    protected String getJoinGatewayTypeString(YExternalNetElement netElement) {
        if (netElement instanceof YTask task) {
            if (task.getPresetElements().size() > 1) {
                return toCamelCase(DescriptionUtil.getGatewayType(task.getJoinType()).toString());
            }
        }
        return toCamelCase(GatewayType.None.toString());
    }

    private String toCamelCase(String original) {
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
