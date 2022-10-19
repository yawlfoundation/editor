package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.analyser.util.alloy.utils.DescriptionUtil;
import org.yawlfoundation.yawl.analyser.util.alloy.utils.GatewayType;
import org.yawlfoundation.yawl.elements.*;

import java.util.ArrayList;
import java.util.Iterator;

public class InputConditionOutputDescriptor {
    private final YInputCondition inputCondition;
    private final String _toTransformOrJoin;

    public InputConditionOutputDescriptor(YInputCondition inputCondition, String toTransformOrJoin) {
        this.inputCondition = inputCondition;
        this._toTransformOrJoin = toTransformOrJoin;
    }

    public String getOutputDescription() {
        return String.format("""
                               
                fact{ all s: State | all i: input_condition |  i in s.token =>
                %s
                }
                                
                """, getOutputsOfInputConditionDescriptions());

    }

    private String getOutputsOfInputConditionDescriptions() {
        ArrayList<String> descriptions = new ArrayList<>();
        for (YExternalNetElement outputElement : this.inputCondition.getPostsetElements()) {
            descriptions.add(String.format("""
                    { one t: Object1 | t in i.flowsInto.nextTask && t%s}
                    """, this._getOutputElementDescription(outputElement)));
        }
        return String.join(" && \n", descriptions);
    }

    private String _getOutputElementDescription(YExternalNetElement outputElement) {
        if (outputElement instanceof YOutputCondition) {
            return " = output_condition";
        } else if (outputElement instanceof YTask outputTask) {
            return String.format(".label = \"%s\" && t.join = \"%s\" && t.split = \"%s\" && t in s.next.token",
                    outputTask.getName(), this.getJoinGatewayTypeString(outputTask),
                    this.getSplitGatewayTypeString(outputTask));
        }
        else {
            YTask outputTask = (YTask) outputElement.getPostsetElements().iterator().next();
            return String.format(".label = \"%s\" && t.join = \"%s\" && t.split = \"%s\" && t in s.next.token",
                    outputTask.getName(), this.getJoinGatewayTypeString(outputTask),
                    this.getSplitGatewayTypeString(outputTask));
        }
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
                GatewayType gatewayType = DescriptionUtil.getGatewayType(task.getJoinType());
                if (this._toTransformOrJoin != null &&
                        this._toTransformOrJoin.equals(task.getName()) && gatewayType == GatewayType.or)
                    return toCamelCase(GatewayType.xor.toString());
                return toCamelCase(gatewayType.toString());
            }
        }
        return toCamelCase(GatewayType.None.toString());
    }

    private String toCamelCase(String original) {
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
