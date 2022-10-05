package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YInputCondition;

import java.util.ArrayList;
import java.util.Iterator;

public class InputConditionOutputDescriptor {
    private final YInputCondition inputCondition;
    public InputConditionOutputDescriptor(YInputCondition inputCondition) {
        this.inputCondition = inputCondition;
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
                    { one f:i.flowsInto | f.nextTask.label = "%s" && f.nextTask in s.next.token}
                    """, outputElement.getName()));
        }
        return String.join(" && \n", descriptions);
    }
}
