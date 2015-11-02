package org.yawlfoundation.yawl.editor.ui.properties.data.validation;

import org.yawlfoundation.yawl.editor.ui.properties.data.*;
import org.yawlfoundation.yawl.editor.ui.properties.data.binding.OutputBindings;
import org.yawlfoundation.yawl.util.StringUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Revalidates a task variable's data bindings after its data type is changed. Also
 * revalidates all referencing task variable bindings when a net variable's data
 * type is changed. Used by DataVariableDialog.
 *
 * @author Michael Adams
 * @date 29/09/15
 */
public class DataTypeChangeValidator {

    // the calling net or task level table panel
    private final VariableTablePanel tablePanel;


    public DataTypeChangeValidator(VariableTablePanel panel) { tablePanel = panel; }


    /**
     * Validates the bindings of a variable for a change of data type
     * @param row the variable row that is changing its data type. Note that the row
     *            currently retains its old data type. The outcome will be the row
     *            is marked for validity for each binding
     * @param newType the data type the variable row is changing to
     */
    public void validateBindings(VariableRow row, String newType) {
        if (newType == null || newType.equals(row.getDataType())) return;    // no change

        // if the row is a net variable, validate all task mappings against it
        if (tablePanel instanceof NetVariableTablePanel) {
            revalidateTaskBindings(row, newType);
            return;
        }

        // otherwise validate this task variable's bindings against new data type
        if (row.isInput() || row.isInputOutput()) {
            row.setValidInputBinding(
                    validateBinding(getNetVariables(), newType, row.getBinding(), null));
        }
        if (row.isOutput() || row.isInputOutput()) {
            validateOutputBinding(row, newType);
        }

        // update icons if binding is no longer valid
        ((TaskVariableTablePanel) tablePanel).setBindingIconsForSelection();

        row.setValidValue(true);                 // varrow will re-initialise value
    }


    /**
     * Revalidates all referencing task variable bindings when a net variable changes its
     * data type
     * @param netVar the net-level variable changing its data type
     * @param newType the data type being changed to
     */
    public void revalidateTaskBindings(VariableRow netVar, String newType) {
        List<VariableRow> taskVars = getTaskVariables();
        if (taskVars.isEmpty()) return;                    // no task vars to revalidate
        List<VariableRow> netVars = getNetVariables();
        BindingTypeValidator inputValidator = newBindingTypeValidator(netVars, null, null);
        BindingTypeValidator outputValidator = newBindingTypeValidator(netVars, taskVars,
                newType);
        revalidateTaskBindings(netVar, newType, inputValidator, outputValidator);
    }



    /**
     * Revalidates all referencing task variable bindings when a net variable changes its
     * data type
     * @param netVar the net-level variable changing its data type
     * @param newType the data type being changed to
     */
    public void revalidateTaskBindings(VariableRow netVar, String newType,
                                       BindingTypeValidator inputValidator,
                                       BindingTypeValidator outputValidator) {

        String origType = netVar.getDataType();
        String origValue = netVar.getValue();
        netVar.setDataType(newType);
        for (VariableRow row : getTaskVariables()) {
            validateInputBinding(inputValidator, row);

            if (row.isOutput() || row.isInputOutput()) {
                String outputBinding = getOutputBindings().getBindingFromSource(
                        row.getName());
                if (outputBinding != null) {
                    VariableRow target = getOutputBindingTarget(outputBinding, row.getName());
                    if (target != null && target.getName().equals(netVar.getName())) {
                        if (row.isMultiInstance()) {
                            validateMIRowOutputBinding(row, outputBinding,
                                    outputValidator, target.getDataType());
                        }
                        else {
                            validateRowOutputBinding(row, outputBinding, outputValidator);
                        }
                    }
                }
            }
        }
        netVar.setDataType(origType);
        netVar.setValue(origValue);
        getVariableDialog().getTaskTablePanel().setBindingIconsForSelection();
    }


    /**
     * Revalidates all task mappings against each net variable
     */
    public void revalidateTaskBindingsInBackground() {

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                List<VariableRow> taskVars = getTaskVariables();
                if (! taskVars.isEmpty()) {                      // no vars to revalidate
                    List<VariableRow> netVars = getNetVariables();
                    BindingTypeValidator inputValidator =
                            newBindingTypeValidator(netVars, null, null);
                    BindingTypeValidator outputValidator =
                            newBindingTypeValidator(netVars, taskVars, null);

                    for (VariableRow netVar : netVars) {
                        String dataType = netVar.getDataType();
                        outputValidator.setDataType(dataType);
                        revalidateTaskBindings(netVar, dataType,
                                inputValidator, outputValidator);
                    }
                }
                return null;
            }

            protected void done() { getVariableDialog().enableButtonsIfValid(); }

        }.execute();

    }


    /**
     * Instantiates a binding type validator and uses it to validate a binding
     * @param variables the set of variables to validate against
     * @param dataType the data type to validate
     * @param binding the binding to validate against the new data type
     * @param taskDecompositionID the decomp id; may be null for net variables
     * @return true if the binding is valid for the data type
     */
    private boolean validateBinding(List<VariableRow> variables, String dataType,
                                    String binding, String taskDecompositionID) {
        if (StringUtil.isNullOrEmpty(binding)) {
            return false;                             // no binding = invalid
        }

        BindingTypeValidator validator = new BindingTypeValidator(variables, dataType,
                taskDecompositionID);
        validator.waitForInitialisation(2000);
        return validateBinding(validator, binding);
    }


    /**
     * Validates a binding using the constructed validator
     * @param validator a binding type validator constructed for this validation
     * @param binding  the binding to validate
     * @return true if the binding is valid for data type
     */
    private boolean validateBinding(BindingTypeValidator validator, String binding) {
        return validator.isInitialised() && validator.validate(binding).isEmpty();
    }


    /**
     * Validates the output binding of a task variable (if any) against a change of
     * data type
     * @param row the task variable row
     * @param newType the data type being changed to
     */
    private void validateOutputBinding(VariableRow row, String newType) {
        List<VariableRow> netVars = getNetVariables();

        // if this variable row has an output binding
        OutputBindings outputBindings = getOutputBindings();
        String outputBinding = outputBindings.getBindingFromSource(row.getName());
        if (outputBinding != null) {

            // find target net variable
            VariableRow target = getOutputBindingTarget(outputBinding, row.getName());;
            if (target != null) {

                // validate it
                String origType = row.getDataType();
                String origValue = row.getValue();
                row.setDataType(newType);
                List<VariableRow> allVars = new ArrayList<VariableRow>(netVars);
                allVars.addAll(getTaskVariables());
                row.setValidOutputBinding(validateBinding(allVars, target.getDataType(),
                        outputBinding, row.getDecompositionID()));
                row.setDataType(origType);
                row.setValue(origValue);
            }
        }
    }


    /**
     * Gets the target net variable of a task variable output binding
     * @param binding the task variable output binding
     * @param taskVarName the name of the variable
     * @return the target net level variable, or null if there is no output binding, or
     * if the target could not be determined
     */
    private VariableRow getOutputBindingTarget(String binding, String taskVarName) {
        if (binding != null) {

            // find target net variable
            String targetName = getOutputBindings().getTarget(taskVarName);
            return getVariableByName(getNetVariables(), targetName);
        }
        return null;
    }


    /**
     * Gets the variable row with a matching name from a list of rows
     * @param variableRows the list to search
     * @param name the name to find
     * @return the matching row, or null if not found
     */
    private VariableRow getVariableByName(List<VariableRow> variableRows, String name) {
        for (VariableRow variableRow : variableRows) {
            if (variableRow.getName().equals(name)) {
                return variableRow;
            }
        }
        return null;
    }


    /**
     * Constructs a binding type validator object. For input binding validators, the
     * list of task variables and the data type will be null.
     * @param netVars the list of net variables
     * @param taskVars the list of task variables. Precondition: if not null, the list
     *                 contains at least one item.
     * @param dataType the data type to be validated against
     * @return the constructed binding type validator
     */
    private BindingTypeValidator newBindingTypeValidator(List<VariableRow> netVars,
              List<VariableRow> taskVars, String dataType) {
        List<VariableRow> vars = new ArrayList<VariableRow>(netVars);
        String taskDecompositionID = null;
        if (taskVars != null) {
            vars.addAll(taskVars);
            taskDecompositionID = taskVars.get(0).getDecompositionID();
        }
        BindingTypeValidator validator =
                new BindingTypeValidator(vars, dataType, taskDecompositionID);
        validator.waitForInitialisation(2000);
        return validator;
    }


    // use the validator to validate an input binding
    private void validateInputBinding(BindingTypeValidator validator, VariableRow taskVar) {
        if (taskVar.isInput() || taskVar.isInputOutput()) {
            validator.setDataType(taskVar.getDataType());
            taskVar.setValidInputBinding(validateBinding(validator, taskVar.getBinding()));
        }
    }


    // return the current list of net level variables
    private List<VariableRow> getNetVariables() {
        return getVariableDialog().getNetTable().getVariables();
    }


    // return the current list of task level variables for the active task
    private List<VariableRow> getTaskVariables() {
        VariableTable taskTable = getVariableDialog().getTaskTable();
        return taskTable != null ? taskTable.getVariables() :
                Collections.<VariableRow>emptyList();
    }


    // return the list of output bindings for the active task
    private OutputBindings getOutputBindings() {
        return getVariableDialog().getOutputBindings();
    }


    private void validateRowOutputBinding(VariableRow row, String binding,
                                   BindingTypeValidator validator) {
        row.setValidOutputBinding(validateBinding(validator, binding));
    }


    // an row set as multi-instance has its data type set to the child type of the
    // corresponding net level var, so we need to validate against the row's own type
    private void validateMIRowOutputBinding(VariableRow row, String binding,
                               BindingTypeValidator validator, String targetType) {
        validator.setDataType(row.getDataType());
        validateRowOutputBinding(row, binding, validator);
        validator.setDataType(targetType);
    }


    private DataVariableDialog getVariableDialog() {
        return tablePanel.getVariableDialog();
    }

}
