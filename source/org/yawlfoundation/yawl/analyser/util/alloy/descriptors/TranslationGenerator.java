package org.yawlfoundation.yawl.analyser.util.alloy.descriptors;

import org.yawlfoundation.yawl.analyser.util.alloy.Constants;
import org.yawlfoundation.yawl.analyser.util.alloy.utils.DescriptionUtil;
import org.yawlfoundation.yawl.analyser.util.alloy.utils.GatewayType;
import org.yawlfoundation.yawl.analyser.util.alloy.utils.VariableDataTypeMapping;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.elements.data.YVariable;

import java.util.ArrayList;
import java.util.Map;

public class TranslationGenerator {
    private final YNet _workFlow;

    public TranslationGenerator(YNet workFlow) {
        this._workFlow = workFlow;
    }

    public String generate() {
        StringBuilder translationBuilder = new StringBuilder();
        translationBuilder.append(this._generate_open_order());
        translationBuilder.append(this._generateStateSignature());
        translationBuilder.append(Constants.staticAlloyDefinitions);
        translationBuilder.append(inputConditionOutputDescriptorFactory().getOutputDescription());
        for (int i = 0; i < this._workFlow.getNetTasks().size(); i++) {
            YTask currentTask = this._workFlow.getNetTasks().get(i);
            System.out.println("-----------------------------------");
            System.out.println(currentTask.getInformation());
            System.out.println(currentTask.getName());
            System.out.println("-----------------------------------");
            translationBuilder.append(inputDescriptorFactory(currentTask).getInputDescription());
            translationBuilder.append(outputDescriptorFactory(currentTask).getOutputDescription());
        }
        translationBuilder.append(DescriptionUtil.getShowPredPart(this._workFlow.getNetTasks().size() + 2, this._getPredicateCount()));
        return translationBuilder.toString();
    }

    private String _generate_open_order() {
        return """
                /* Impose an ordering on the State. */
            open util/ordering[State]
            """;
    }

    private String _generateStateSignature() {
        String stateSignature = ("sig State {\n \ttoken, n_token: some Object1,\n");
        String[] variableDescriptions = new String[this._workFlow.getLocalVariables().size()];
        ArrayList<String> variableNames = new ArrayList<>(this._workFlow.getLocalVariables().keySet());
        Map<String, YVariable> variablesMap = this._workFlow.getLocalVariables();
        for (int i = 0; i < variableNames.size(); i++) {
            variableDescriptions[i] = String.format("\t%s: lone %s", variablesMap.get(variableNames.get(i)).getName(),
                    VariableDataTypeMapping.mapping.get(variablesMap.get(variableNames.get(i)).getDataTypeName()));
        }
        stateSignature += String.format("%s, \n}%n%n", String.join(", \n", variableDescriptions));
        return stateSignature;
    }

    private InputDescriptor inputDescriptorFactory(YTask task) {
        GatewayType joinType = DescriptionUtil.getGatewayType(task.getJoinType());
        if (task.getPresetFlows().size() > 1) {
            return new NotNoneJoinInputDescriptor(task, this.getVariableNames());
        }
        return new NoneJoinInputDescriptor(task, this.getVariableNames());
    }

    private InputConditionOutputDescriptor inputConditionOutputDescriptorFactory(){
        return new InputConditionOutputDescriptor(this._workFlow.getInputCondition());
    }

    private ArrayList<String> getVariableNames() {
        return new ArrayList<String>(this._workFlow.getLocalVariables().keySet());
    }

    private OutputDescriptor outputDescriptorFactory(YTask task) {
        GatewayType splitType = DescriptionUtil.getGatewayType(task.getSplitType());
        if (task.getPostsetFlows().size() > 1)
            return new NotNoneSplitOutputDescriptor(task, this.getVariableNames());
        return new NoneSplitOutputDescriptor(task, this.getVariableNames());
    }

    private int _getPredicateCount() {
        int conditionCount = this._workFlow.getInputCondition().getPostsetFlows().size();
        for (YTask task : this._workFlow.getNetTasks()) {
            conditionCount += task.getPostsetFlows().size();
        }
        return conditionCount;
    }
}
