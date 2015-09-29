package org.yawlfoundation.yawl.editor.ui.properties.data.validation;

import org.yawlfoundation.yawl.editor.ui.properties.data.*;
import org.yawlfoundation.yawl.editor.ui.properties.data.binding.OutputBindings;
import org.yawlfoundation.yawl.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael Adams
 * @date 29/09/15
 */
public class DataTypeChangeValidator {

    private final VariableTablePanel tablePanel;


    public DataTypeChangeValidator(VariableTablePanel panel) { tablePanel = panel; }


    public void validateBindings(VariableRow row, String newType) {
        if (newType == null || newType.equals(row.getDataType())) return;    // no change

        DataVariableDialog dialog = tablePanel.getVariableDialog();
        List<VariableRow> netVars = dialog.getNetTable().getVariables();
        List<VariableRow> taskVars = dialog.getTaskTable() != null ?
                dialog.getTaskTable().getVariables() : Collections.<VariableRow>emptyList();

        if (tablePanel instanceof NetVariableTablePanel) {
            invalidateTaskMappings(netVars, taskVars, row, newType);
            return;
        }


        // check mappings are still valid for new data type
        if (row.isInput() || row.isInputOutput()) {
            row.setValidInputBinding(
                    validateBinding(netVars, newType, row.getBinding(), null));
        }

        if (row.isOutput() || row.isInputOutput()) {
            OutputBindings outputBindings = tablePanel.getVariableDialog().getOutputBindings();
            String outputBinding = outputBindings.getBindingFromSource(row.getName());
            if (outputBinding != null) {
                String targetName = outputBindings.getTarget(row.getName());
                VariableRow target = getVariableByName(netVars, targetName);
                if (target != null) {
                    String origType = row.getDataType();
                    row.setDataType(newType);
                    List<VariableRow> allVars = new ArrayList<VariableRow>(netVars);
                    allVars.addAll(taskVars);
                    row.setValidOutputBinding(validateBinding(allVars, target.getDataType(),
                            outputBinding, row.getDecompositionID()));
                    row.setDataType(origType);
                }
            }
        }
        ((TaskVariableTablePanel) tablePanel).setBindingIconsForSelection();

        row.setValidValue(true);                 // varrow will re-initialise value
    }


    private boolean validateBinding(List<VariableRow> variables, String dataType,
                                    String binding, String taskDecompositionID) {
        if (StringUtil.isNullOrEmpty(binding)) {
            return false;                             // no binding = invalid
        }

        BindingTypeValidator validator = new BindingTypeValidator(variables, dataType,
                taskDecompositionID);
        return validateBinding(validator, binding);
    }


    private boolean validateBinding(BindingTypeValidator validator, String binding) {
        int maxWait = 1200;
        while (! validator.isInitialised() && maxWait > 0) {
            try {
                Thread.sleep(200);
                maxWait -= 200;
            }
            catch (InterruptedException ie) {
                // continue
            }
        }

        return validator.isInitialised() && validator.validate(binding).isEmpty();
    }


    private void invalidateTaskMappings(List<VariableRow> netVars,
                                        List<VariableRow> taskVars,
                                        VariableRow netVar, String newType) {
        if (taskVars.isEmpty()) return;

        String origType = netVar.getDataType();
        netVar.setDataType(newType);
        OutputBindings outputBindings = tablePanel.getVariableDialog().getOutputBindings();
        BindingTypeValidator inputValidator = newBindingTypeValidator(netVars, null, null);
        BindingTypeValidator outputValidator = newBindingTypeValidator(netVars, taskVars,
                newType);
        for (VariableRow row : taskVars) {
            validateInputBinding(inputValidator, row);

            if (row.isOutput() || row.isInputOutput()) {
                String outputBinding = outputBindings.getBindingFromSource(row.getName());
                if (outputBinding != null) {
                    String targetName = outputBindings.getTarget(row.getName());
                    VariableRow target = getVariableByName(netVars, targetName);
                    if (target != null && target.getName().equals(netVar.getName())) {
                        row.setValidOutputBinding(validateBinding(outputValidator,
                                outputBinding));
                    }
                }
            }
        }
        netVar.setDataType(origType);
        tablePanel.getVariableDialog().getTaskTablePanel().setBindingIconsForSelection();
    }


    private VariableRow getVariableByName(List<VariableRow> variableRows, String name) {
        for (VariableRow variableRow : variableRows) {
            if (variableRow.getName().equals(name)) {
                return variableRow;
            }
        }
        return null;
    }


    // pre: ! isNullOrEmpty(taskVars)
    private BindingTypeValidator newBindingTypeValidator(List<VariableRow> netVars,
              List<VariableRow> taskVars, String dataType) {
        List<VariableRow> vars = new ArrayList<VariableRow>(netVars);
        String taskDecompositionID = null;
        if (taskVars != null) {
            vars.addAll(taskVars);
            taskDecompositionID = taskVars.get(0).getDecompositionID();
        }
        return new BindingTypeValidator(vars, dataType, taskDecompositionID);
    }


    private void validateInputBinding(BindingTypeValidator validator, VariableRow taskVar) {
        validator.setDataType(taskVar.getDataType());
        if (taskVar.isInput() || taskVar.isInputOutput()) {
            taskVar.setValidInputBinding(validateBinding(validator, taskVar.getBinding()));
        }
    }

}
